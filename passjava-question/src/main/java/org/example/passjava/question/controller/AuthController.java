package org.example.passjava.question.controller;

import jakarta.validation.Valid;
import org.example.passjava.question.common.Result;
import org.example.passjava.question.dto.UmsUserLoginDTO;
import org.example.passjava.question.dto.UmsUserRegisterDTO;
import org.example.passjava.question.service.AuthService;
import org.example.passjava.question.vo.LoginVO;
import org.example.passjava.question.vo.UmsUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 注册
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UmsUserRegisterDTO dto) {
        Boolean b = authService.register(dto);
        if(b)
            return Result.success(dto,"注册成功");
        return Result.error(500, "注册失败");
    }

    /**
     * 登录
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UmsUserLoginDTO dto) {
        LoginVO login = authService.login(dto);
        if(login != null)
            return Result.success(login,"登录成功");
        return Result.error(500, "登录失败");
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    @GetMapping("/me")
    public Result<UmsUserVO> me(@AuthenticationPrincipal Jwt jwt) {
        UmsUserVO umsUserVO = authService.me(jwt);
        return Result.success(umsUserVO,"获取用户信息成功");
    }
}
