package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.passjava.study.entity.SmsViewLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 123456
* @description 针对表【sms_view_log】的数据库操作Mapper
* @createDate 2026-10-23 14:34:51
* @Entity org.example.passjava.study.entity.SmsViewLog
*/
@Mapper
public interface SmsViewLogMapper extends BaseMapper<SmsViewLog> {
    @Insert("""
        INSERT INTO sms_view_log
            (user_id, question_id, view_count, last_view_time)
        VALUES
            (#{userId}, #{questionId}, 1, NOW())
        ON DUPLICATE KEY UPDATE
            view_count = view_count + 1,
            last_view_time = NOW()
        """)
    int recordView(
            @Param("userId") Long userId,
            @Param("questionId") Long questionId
    );
}




