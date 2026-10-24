package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.passjava.study.entity.SmsDailyStat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;

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
}




