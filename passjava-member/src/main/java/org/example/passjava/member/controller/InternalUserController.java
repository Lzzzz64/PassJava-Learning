package org.example.passjava.member.controller;

import org.example.passjava.member.common.Result;
import org.example.passjava.member.entity.UmsUser;
import org.example.passjava.member.service.InternalUserService;
import org.example.passjava.member.service.UmsUserService;
import org.example.passjava.member.vo.UmsUserSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalUserController {
    @Autowired
    private InternalUserService internalUserService;

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<UmsUserSummaryVO> getUser(@PathVariable Long id) {
        UmsUserSummaryVO umsUserSummaryVO = internalUserService.getUser(id);
        return Result.success(umsUserSummaryVO, "获取用户信息成功");
    }
}
