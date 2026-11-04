package org.example.passjava.study.integration;


import org.example.passjava.study.mapper.SmsViewLogMapper;
import org.example.passjava.study.support.StudyTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "app.jwt.secret=U8DDPRiGakUDEwVRjxxRkMP1c1RfYj0W8SmE+InP83c="
})
class ViewLogConcurrencyIntegrationTest
        extends StudyTestcontainers {

    @Autowired
    private SmsViewLogMapper viewLogMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql("/sql/04_study.sql")
    void concurrentViewsShouldNotLoseUpdates()
            throws Exception {

        long userId = 900001L;
        long questionId = 900003L;

        // 保证本次测试起点明确
        jdbcTemplate.update(
                """
                DELETE FROM sms_view_log
                WHERE user_id = ? AND question_id = ?
                """,
                userId, questionId
        );

        int tasks = 100;

        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        12, 12,
                        0, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(120)
                );

        CountDownLatch ready = new CountDownLatch(tasks);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < tasks; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();

                    viewLogMapper.recordView(userId, questionId);

                    return null;
                }));
            }

            // 注意：此处不能等待100个任务全部开始，
            // 因为线程池只有12个线程，其余任务在队列中。
            // 因此只需要确保第一批线程已经准备好。
            assertTrue(
                    waitForFirstWorkers(ready, tasks, 12)
            );

            // 同时放行已准备的工作线程
            start.countDown();

            // Future.get 会将子线程异常传递回来
            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }

            Integer actual = jdbcTemplate.queryForObject(
                    """
                    SELECT view_count
                    FROM sms_view_log
                    WHERE user_id = ? AND question_id = ?
                    """,
                    Integer.class,
                    userId, questionId
            );

            assertEquals(tasks, actual);

        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    private boolean waitForFirstWorkers(
            CountDownLatch ready,
            int total,
            int workers) throws InterruptedException {

        long deadline = System.nanoTime()
                + TimeUnit.SECONDS.toNanos(10);

        while (ready.getCount() > total - workers) {
            if (System.nanoTime() >= deadline) {
                return false;
            }
            Thread.sleep(10);
        }

        return true;
    }
}
