package org.example.passjava.member.service.impl;

import org.example.passjava.member.entity.UmsUser;
import org.example.passjava.member.mapper.UmsUserMapper;
import org.example.passjava.member.service.InternalUserService;
import org.example.passjava.member.vo.UmsUserSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InternalUserServiceImpl implements InternalUserService {
    @Autowired
    private UmsUserMapper umsUserMapper;

    @Override
    public UmsUserSummaryVO getUser(Long id) {
        UmsUser umsUser = umsUserMapper.selectById(id);
        if(umsUser == null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "用户名不存在或被禁用");

        UmsUserSummaryVO umsUserSummaryVO = new UmsUserSummaryVO();
        umsUserSummaryVO.setId(umsUser.getId());
        umsUserSummaryVO.setUsername(umsUser.getUsername());
        umsUserSummaryVO.setNickname(umsUser.getNickname());
        return umsUserSummaryVO;
    }
}
