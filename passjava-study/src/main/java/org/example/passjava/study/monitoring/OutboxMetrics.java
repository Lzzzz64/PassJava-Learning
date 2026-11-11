
package org.example.passjava.study.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class OutboxMetrics {

    private final JdbcTemplate jdbcTemplate;

    private final AtomicLong pending = new AtomicLong();
    private final AtomicLong sending = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();

    public OutboxMetrics(
            JdbcTemplate jdbcTemplate,
            MeterRegistry registry) {

        this.jdbcTemplate = jdbcTemplate;

        Gauge.builder("passjava.outbox.pending", pending,
                        AtomicLong::get)
                .register(registry);

        Gauge.builder("passjava.outbox.sending", sending,
                        AtomicLong::get)
                .register(registry);

        Gauge.builder("passjava.outbox.failed", failed,
                        AtomicLong::get)
                .register(registry);
    }

    @Scheduled(fixedDelay = 15000)
    public void refresh() {

        try {
            pending.set(countByStatus(0));
            sending.set(countByStatus(1));
            failed.set(countByStatus(3));

        } catch (Exception ex) {
            // 测量失败不应该中断业务任务。
            // 实际部署时应记录限流后的日志，并添加采集健康指标。
            org.slf4j.LoggerFactory
                    .getLogger(OutboxMetrics.class)
                    .warn("刷新Outbox监控指标失败", ex);
        }
    }

    private long countByStatus(int status) {
        Long count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM sms_event_outbox
                WHERE status = ?
                """,
                Long.class,
                status
        );

        return count == null ? 0 : count;
    }
}
