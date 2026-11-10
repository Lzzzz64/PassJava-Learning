package org.example.passjava.study.integration;

import org.example.passjava.study.support.StudyTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "app.jwt.secret=U8DDPRiGakUDEwVRjxxRkMP1c1RfYj0W8SmE+InP83c="
})
public class OutboxConcurrentClaimTest extends StudyTestcontainers {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql("/sql/04_study.sql")
    void onlyOnePublisherCanClaimSameEvent()
            throws Exception {

        String eventId = UUID.randomUUID().toString();

        jdbcTemplate.update(
                """
                INSERT INTO sms_event_outbox (
                    event_id,
                    user_id,
                    question_id,
                    occurred_at,
                    status,
                    retry_count,
                    next_retry_at
                )
                VALUES (?, 900001, 3, NOW(), 0, 0, NOW())
                """,
                eventId
        );

        int workers = 10;

        ExecutorService executor =
                Executors.newFixedThreadPool(workers);

        CountDownLatch ready =
                new CountDownLatch(workers);

        CountDownLatch start =
                new CountDownLatch(1);

        List<Future<Integer>> futures =
                new ArrayList<>();

        try {
            for (int i = 0; i < workers; i++) {

                futures.add(executor.submit(() -> {

                    ready.countDown();

                    if (!start.await(
                            10, TimeUnit.SECONDS)) {
                        throw new TimeoutException(
                                "等待开始信号超时"
                        );
                    }

                    return jdbcTemplate.update(
                            """
                            UPDATE sms_event_outbox
                            SET status = 1,
                                locked_at = NOW(),
                                retry_count = retry_count + 1
                            WHERE event_id = ?
                              AND status = 0
                            """,
                            eventId
                    );
                }));
            }

            assertTrue(
                    ready.await(10, TimeUnit.SECONDS),
                    "工作线程没有全部准备好"
            );

            start.countDown();

            int successCount = 0;

            for (Future<Integer> future : futures) {
                successCount +=
                        future.get(30, TimeUnit.SECONDS);
            }

            assertEquals(1, successCount);

            Integer status = jdbcTemplate.queryForObject(
                    """
                    SELECT status
                    FROM sms_event_outbox
                    WHERE event_id = ?
                    """,
                    Integer.class,
                    eventId
            );

            Integer retryCount = jdbcTemplate.queryForObject(
                    """
                    SELECT retry_count
                    FROM sms_event_outbox
                    WHERE event_id = ?
                    """,
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
