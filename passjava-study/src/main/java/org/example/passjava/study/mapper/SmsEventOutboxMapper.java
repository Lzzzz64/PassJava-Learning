package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.passjava.study.entity.SmsEventOutbox;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author 123456
* @description 针对表【sms_event_outbox】的数据库操作Mapper
* @createDate 2026-10-24 19:28:08
* @Entity org.example.passjava.study.entity.SmsEventOutbox
*/
@Mapper
public interface SmsEventOutboxMapper extends BaseMapper<SmsEventOutbox> {
    @Select("""
        SELECT *
        FROM sms_event_outbox
        WHERE status = 0
          AND next_retry_at <= NOW()
        ORDER BY create_time ASC
        LIMIT 50
        """)
    //查询等待发送的消息
    List<SmsEventOutbox> findPending();

    @Update("""
        UPDATE sms_event_outbox
        SET status = 1,
            locked_at = NOW(),
            retry_count = retry_count + 1
        WHERE event_id = #{eventId}
          AND status = 0
        """)
    //抢占消息，防止多个任务同时发送
    int claim(@Param("eventId") String eventId);

    @Update("""
        UPDATE sms_event_outbox
        SET status = 2,
            locked_at = NULL,
            last_error = NULL
        WHERE event_id = #{eventId}
          AND status = 1
        """)
    //消息发送成功，更新状态
    int markSent(@Param("eventId") String eventId);

    @Update("""
        UPDATE sms_event_outbox
        SET status = #{status},
            locked_at = NULL,
            next_retry_at = #{nextRetryAt},
            last_error = #{error}
        WHERE event_id = #{eventId}
          AND status = 1
        """)
    //消息发送失败，设置重试时间
    int markFailure(
            @Param("eventId") String eventId,
            @Param("status") int status,
            @Param("nextRetryAt") Date nextRetryAt,
            @Param("error") String error
    );

    @Update("""
        UPDATE sms_event_outbox
        SET status = 0,
            locked_at = NULL,
            next_retry_at = NOW()
        WHERE status = 1
          AND locked_at < DATE_SUB(NOW(), INTERVAL 5 MINUTE)
        """)
    //恢复因程序异常而卡住的消息
    int recoverTimedOut();
}




