package org.example.passjava.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.passjava.member.entity.UmsUser;
import org.example.passjava.member.mapper.UmsUserMapper;
import org.example.passjava.member.service.UmsUserService;
import org.springframework.stereotype.Service;

/**
* @author 123456
* @description 针对表【ums_user】的数据库操作Service实现
* @createDate 2026-10-20 15:21:44
*/
@Service
public class UmsUserServiceImpl extends ServiceImpl<UmsUserMapper, UmsUser>
    implements UmsUserService{

}




