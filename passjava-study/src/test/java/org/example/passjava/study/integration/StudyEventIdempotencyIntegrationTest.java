package org.example.passjava.study.integration;

import org.example.passjava.study.entity.SmsEventOutbox;
import org.example.passjava.study.service.QmsQuestionCheckService;
import org.example.passjava.study.service.SmsStudyViewCommandService;
import org.example.passjava.study.mapper.SmsEventOutboxMapper;

import org.example.passjava.study.support.StudyTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

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
class StudyEventIdempotencyIntegrationTest
        extends StudyTestcontainers {

    @Autowired
    private SmsStudyViewCommandService commandService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SmsEventOutboxMapper outboxEventMapper;

    @MockitoBean
    private QmsQuestionCheckService qmsQuestionCheckService;

    @Test
    @Sql("/sql/04_study.sql")
    void shouldRollbackViewWhenOutboxInsertFails() {

        Long userId = 1L;
        Long questionId = 3L;

        int before = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sms_view_log WHERE user_id = ? AND question_id = ?",
                Integer.class,
                userId,
                questionId
        );

        doNothing().when(qmsQuestionCheckService).checkExists(questionId);

        doThrow(new RuntimeException("模拟Outbox写入失败"))
                .when(outboxEventMapper)
                .insert(any(SmsEventOutbox.class));

        assertThrows(
                RuntimeException.class,
                () -> commandService.recordAndCreateEvent(
                        userId, questionId
                )
        );

        int after = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sms_view_log WHERE user_id = ? AND question_id = ?",
                Integer.class,
                userId,
                questionId
        );

        assertEquals(before, after);
    }
}
