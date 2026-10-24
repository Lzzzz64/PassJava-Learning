package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.*;
import org.example.passjava.study.VO.SmsDailyStatVO;
import org.example.passjava.study.entity.SmsDailyStat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
* @author 123456
* @description 针对表【sms_daily_stat】的数据库操作Mapper
* @createDate 2026-10-24 11:59:52
* @Entity org.example.passjava.study.entity.SmsDailyStat
*/
@Mapper
public interface SmsDailyStatMapper extends BaseMapper<SmsDailyStat> {
    @Insert("""
        INSERT INTO sms_daily_stat
            (user_id, study_date, view_count)
        VALUES (#{userId}, DATE(#{studyDate}), 1)
        ON DUPLICATE KEY UPDATE
            view_count = view_count + 1
        """)
    //统计每天学习的次数，使用DATA()，将时间转换为日期
    int increase(
            @Param("userId") Long userId,
            @Param("studyDate") Date studyDate
    );

    @Select("""
    SELECT
        study_date AS date,
        view_count AS viewCount
    FROM sms_daily_stat
    WHERE user_id = #{userId}
      AND study_date BETWEEN #{start} AND #{end}
    ORDER BY study_date ASC
    """)
    @Results({
            @Result(column = "date", property = "date"),
            @Result(column = "viewCount", property = "viewCount")
    })
    List<SmsDailyStatVO> selectRange(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}




