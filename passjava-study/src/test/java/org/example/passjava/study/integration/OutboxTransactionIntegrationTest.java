
package org.example.passjava.study.integration;

import org.example.passjava.study.entity.SmsEventOutbox;
import org.example.passjava.study.entity.SmsStudyViewEvent;
import org.example.passjava.study.mapper.SmsDailyStatMapper;
import org.example.passjava.study.mapper.SmsProcessedEventMapper;
import org.example.passjava.study.service.QmsQuestionCheckService;
import org.example.passjava.study.service.SmsStudyEventProcessService;
import org.example.passjava.study.service.SmsStudyViewCommandService;
import org.example.passjava.study.mapper.SmsEventOutboxMapper;

import org.example.passjava.study.support.StudyTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "app.jwt.secret=U8DDPRiGakUDEwVRjxxRkMP1c1RfYj0W8SmE+InP83c="
})
class OutboxTransactionIntegrationTest
        extends StudyTestcontainers {

    @Autowired
    private SmsStudyEventProcessService processService;
    @Autowired
    private SmsEventOutboxMapper smsEventOutboxMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql("/sql/04_study.sql")
    void duplicateEventShouldOnlyIncreaseOnce() {

        String eventId = UUID.randomUUID().toString();
        Long userId = 1L;
        Long questionId = 1L;

        // 事件发生时间
        Date date = new Date();

        // 提取事件发生的自然日期
        LocalDate studyDate = date.toInstant()
                .atZone(ZoneId.of("Asia/Shanghai"))
                .toLocalDate();

        SmsStudyViewEvent event =
                new SmsStudyViewEvent(eventId, userId, questionId, date);

        // 1. 查询处理前的学习次数
        Integer before = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(view_count), 0)
                FROM sms_daily_stat
                WHERE user_id = ?
                  AND study_date = ?
                """,
                Integer.class,
                userId,
                java.sql.Date.valueOf(studyDate)
        );

        // 2. 同一个事件处理两次
        processService.process(event);
        processService.process(event);

        // 3. 查询事件处理记录数量
        Integer eventCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM sms_processed_event
                WHERE event_id = ?
                """,
                Integer.class,
                eventId
        );

        // 4. 查询处理后的学习次数
        Integer after = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(view_count), 0)
                FROM sms_daily_stat
                WHERE user_id = ?
                  AND study_date = ?
                """,
                Integer.class,
                userId,
                java.sql.Date.valueOf(studyDate)
        );

        // 5. 验证幂等性
        assertEquals(1, eventCount);
        assertEquals(before + 1, after);
    }


    @Test
    @Sql("/sql/04_study.sql")
    void concurrentDuplicateEventsShouldCountOnce()
            throws Exception {

        int tasks = 20;

        // 使用全新事件ID，避免测试与旧数据冲突
        String eventId = UUID.randomUUID().toString();

        long userId = 900002L;
        long questionId = 900003L;

        SmsStudyViewEvent event = new SmsStudyViewEvent(
                eventId,
                userId,
                questionId,
                new Date()
        );

        ExecutorService executor =
                Executors.newFixedThreadPool(10);

        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < tasks; i++) {
                futures.add(executor.submit(() -> {
                    start.await();

                    processService.process(event);

                    return null;
                }));
            }

            start.countDown();

            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }

            Integer processedCount =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*)
                            FROM sms_processed_event
                            WHERE event_id = ?
                            """,
                            Integer.class,
                            eventId
                    );

            assertEquals(1, processedCount);

            Integer total = jdbcTemplate.queryForObject(
                    """
                    SELECT COALESCE(SUM(view_count), 0)
                    FROM sms_daily_stat
                    WHERE user_id = ?
                    """,
                    Integer.class,
                    userId
            );

            assertEquals(1, total);

        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    @Sql("/sql/04_study.sql")
    void onlyOnePublisherShouldClaimEvent()
            throws Exception {

        String eventId = UUID.randomUUID().toString();

        // 使用 JdbcTemplate 插入满足数据库约束的待发布事件
        jdbcTemplate.update(
                """
                INSERT INTO sms_event_outbox
                    (event_id, user_id, question_id,
                     occurred_at, status, retry_count, next_retry_at)
                VALUES (?, ?, ?, NOW(), 0, 0, NOW())
                """,
                eventId, 900003L, 900003L
        );

        ExecutorService executor =
                Executors.newFixedThreadPool(10);

        CountDownLatch start = new CountDownLatch(1);
        List<Future<Integer>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < 10; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return smsEventOutboxMapper.claim(eventId);
                }));
            }

            start.countDown();

            int successCount = 0;

            for (Future<Integer> future : futures) {
                if (future.get(30, TimeUnit.SECONDS) == 1) {
                    successCount++;
                }
            }

            assertEquals(1, successCount);

            Integer status = jdbcTemplate.queryForObject(
                    "SELECT status FROM sms_event_outbox WHERE event_id = ?",
                    Integer.class,
                    eventId
            );

            Integer retryCount = jdbcTemplate.queryForObject(
                    "SELECT retry_count FROM sms_event_outbox WHERE event_id = ?",
                    Integer.class,
                    eventId
            );

            assertEquals(1, status);
            assertEquals(1, retryCount);

        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

}
