
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
import java.util.Date;
import java.util.UUID;

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
}
