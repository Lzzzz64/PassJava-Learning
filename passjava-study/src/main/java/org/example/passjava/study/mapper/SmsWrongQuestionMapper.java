package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.passjava.study.entity.SmsWrongQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 123456
* @description 针对表【sms_wrong_question】的数据库操作Mapper
* @createDate 2026-10-23 14:35:03
* @Entity org.example.passjava.study.entity.SmsWrongQuestion
*/
@Mapper
public interface SmsWrongQuestionMapper extends BaseMapper<SmsWrongQuestion> {
    @Insert("""
        INSERT INTO sms_wrong_question
            (user_id, question_id, wrong_count, last_wrong_time)
        VALUES
            (#{userId}, #{questionId}, 1, NOW())
        ON DUPLICATE KEY UPDATE
            wrong_count = wrong_count + 1,
            last_wrong_time = NOW()
        """)
    int markWrong(
            @Param("userId") Long userId,
            @Param("questionId") Long questionId
    );
}




