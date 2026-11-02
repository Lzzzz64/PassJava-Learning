package org.example.passjava.service;
import org.example.passjava.study.PassjavaStudyApplication;
import org.example.passjava.study.entity.SmsEventOutbox;
import org.example.passjava.study.mapper.SmsEventOutboxMapper;
import org.example.passjava.study.mapper.SmsViewLogMapper;
import org.example.passjava.study.service.QmsQuestionCheckService;
import org.example.passjava.study.service.impl.SmsStudyViewCommandServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * 第一部分：Mockito 单元测试
 */
@ExtendWith(MockitoExtension.class)
class StudyViewCommandServiceTest {

    @Mock
    private SmsViewLogMapper smsViewLogMapper;

    @Mock
    private QmsQuestionCheckService qmsQuestionCheckService;

    @Mock
    private SmsEventOutboxMapper smsEventOutboxMapper;

    @InjectMocks
    private SmsStudyViewCommandServiceImpl studyViewCommandService;


    // 1. 题目不存在
    @Test
    void shouldFailWhenQuestionNotFound() {

        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "题目不存在"))
                .when(qmsQuestionCheckService)
                .checkExists(1L);

        assertThrows(
                ResponseStatusException.class,
                () -> studyViewCommandService.recordAndCreateEvent(1L, 1L)
        );

        verify(qmsQuestionCheckService).checkExists(1L);

        // 题目不存在，不应该写入查看记录
        verifyNoInteractions(smsViewLogMapper);

        // 不应该创建 Outbox 事件
        verifyNoInteractions(smsEventOutboxMapper);
    }


    // 2. 查看记录写入失败
    @Test
    void shouldFailWhenViewLogInsertFails() {

        when(smsViewLogMapper.recordView(1L, 1L))
                .thenReturn(0);

        assertThrows(
                ResponseStatusException.class,
                () -> studyViewCommandService.recordAndCreateEvent(1L, 1L)
        );

        verify(qmsQuestionCheckService).checkExists(1L);

        verify(smsViewLogMapper).recordView(1L, 1L);

        // 查看记录失败，不应该创建 Outbox
        verifyNoInteractions(smsEventOutboxMapper);
    }


    // 3. Outbox 写入失败
    @Test
    void shouldFailWhenOutboxInsertFails() {

        when(smsViewLogMapper.recordView(1L, 1L))
                .thenReturn(1);

        when(smsEventOutboxMapper.insert(any(SmsEventOutbox.class)))
                .thenReturn(0);

        assertThrows(
                ResponseStatusException.class,
                () -> studyViewCommandService.recordAndCreateEvent(1L, 1L)
        );

        verify(qmsQuestionCheckService).checkExists(1L);

        verify(smsViewLogMapper).recordView(1L, 1L);

        verify(smsEventOutboxMapper)
                .insert(any(SmsEventOutbox.class));
    }


    // 4. 所有操作成功
    @Test
    void shouldRecordViewAndCreateOutboxEventSuccessfully() {

        when(smsViewLogMapper.recordView(1L, 1L))
                .thenReturn(1);

        when(smsEventOutboxMapper.insert(any(SmsEventOutbox.class)))
                .thenReturn(1);

        assertDoesNotThrow(
                () -> studyViewCommandService.recordAndCreateEvent(1L, 1L)
        );

        verify(qmsQuestionCheckService).checkExists(1L);

        verify(smsViewLogMapper).recordView(1L, 1L);

        // 捕获创建的 Outbox 事件
        ArgumentCaptor<SmsEventOutbox> captor =
                ArgumentCaptor.forClass(SmsEventOutbox.class);

        verify(smsEventOutboxMapper).insert(captor.capture());

        SmsEventOutbox event = captor.getValue();

        assertNotNull(event.getEventId());
        assertEquals(1L, event.getUserId());
        assertEquals(1L, event.getQuestionId());
        assertEquals(0, event.getStatus());
        assertNotNull(event.getOccurredAt());
    }
}


/**
 * 第二部分：Spring + MySQL 事务回滚集成测试
 *
 * 注意：
 * 1. 使用真实 Service。
 * 2. 使用真实 SmsViewLogMapper。
 * 3. 仅模拟 SmsEventOutboxMapper，让插入返回 0。
 * 4. 不要在测试方法上添加 @Transactional。
 */
@SpringBootTest(
        classes = PassjavaStudyApplication.class,
        properties = {
                "app.jwt.secret=U8DDPRiGakUDEwVRjxxRkMP1c1RfYj0W8SmE+InP83c="
        }
)
class StudyViewCommandServiceTransactionTest {

    @Autowired
    private SmsStudyViewCommandServiceImpl studyViewCommandService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 模拟题目检查，避免测试依赖远程 Question 服务
    @MockitoBean
    private QmsQuestionCheckService qmsQuestionCheckService;

    // 只模拟 Outbox Mapper
    @MockitoBean
    private SmsEventOutboxMapper smsEventOutboxMapper;


    @AfterEach
    void resetMocks() {
        reset(qmsQuestionCheckService, smsEventOutboxMapper);
    }


    // 5. Outbox 插入失败时，查看记录必须回滚
    @Test
    void shouldRollbackViewLogWhenOutboxInsertFails() {

        Long userId = 1L;
        Long questionId = 3L;

        // 1. 查询操作前的数据库记录数量和学习次数
        Integer countBefore = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM sms_view_log
                WHERE user_id = ? AND question_id = ?
                """,
                Integer.class,
                userId,
                questionId
        );

        Integer viewCountBefore = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(view_count), 0)
                FROM sms_view_log
                WHERE user_id = ? AND question_id = ?
                """,
                Integer.class,
                userId,
                questionId
        );

        // 2. 模拟 Outbox 插入失败
        when(smsEventOutboxMapper.insert(any(SmsEventOutbox.class)))
                .thenReturn(0);

        // 3. 调用真正的 Service，预期抛出异常
        assertThrows(
                ResponseStatusException.class,
                () -> studyViewCommandService.recordAndCreateEvent(
                        userId, questionId
                )
        );

        // 4. 验证 Outbox 插入确实被调用
        verify(smsEventOutboxMapper)
                .insert(any(SmsEventOutbox.class));

        // 5. 再次查询数据库
        Integer countAfter = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM sms_view_log
                WHERE user_id = ? AND question_id = ?
                """,
                Integer.class,
                userId,
                questionId
        );

        Integer viewCountAfter = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(view_count), 0)
                FROM sms_view_log
                WHERE user_id = ? AND question_id = ?
                """,
                Integer.class,
                userId,
                questionId
        );

        // 6. 验证数据库状态没有变化
        assertEquals(
                countBefore,
                countAfter,
                "事务回滚失败：查看记录数量发生变化"
        );

        assertEquals(
                viewCountBefore,
                viewCountAfter,
                "事务回滚失败：学习次数发生变化"
        );
    }
}
