package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.passjava.study.entity.SmsProcessedEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 123456
* @description 针对表【sms_processed_event】的数据库操作Mapper
* @createDate 2026-10-24 12:09:19
* @Entity org.example.passjava.study.entity.SmsProcessedEvent
*/
@Mapper
public interface SmsProcessedEventMapper extends BaseMapper<SmsProcessedEvent> {
    @Insert("""
        INSERT IGNORE INTO sms_processed_event
            (event_id)
        VALUES (#{eventId})
        """)
    //插入数据，如果数据已经存在则忽略，返回受影响的行数
    int insertIfAbsent(@Param("eventId") String eventId);
}




