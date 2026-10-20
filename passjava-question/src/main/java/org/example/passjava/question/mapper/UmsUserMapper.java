package org.example.passjava.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.passjava.question.entity.UmsUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 123456
* @description 针对表【ums_user】的数据库操作Mapper
* @createDate 2026-10-20 15:21:44
* @Entity org.example.passjava.question.entity.UmsUser
*/
@Mapper
public interface UmsUserMapper extends BaseMapper<UmsUser> {

}




