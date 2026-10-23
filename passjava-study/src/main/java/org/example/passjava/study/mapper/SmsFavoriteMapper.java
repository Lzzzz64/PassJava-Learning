package org.example.passjava.study.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.passjava.study.entity.SmsFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 123456
* @description 针对表【sms_favorite】的数据库操作Mapper
* @createDate 2026-10-23 14:34:40
* @Entity org.example.passjava.study.entity.SmsFavorite
*/
@Mapper
public interface SmsFavoriteMapper extends BaseMapper<SmsFavorite> {
    @Insert("""
        INSERT INTO sms_favorite
            (user_id, question_id, create_time)
        VALUES
            (#{userId}, #{questionId}, NOW())
        ON DUPLICATE KEY UPDATE
            id = id
        """)
    int addFavorite(
            @Param("userId") Long userId,
            @Param("questionId") Long questionId
    );
}




