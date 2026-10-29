package org.example.passjava.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.passjava.member.common.Result;
import org.example.passjava.member.dto.UmsUserLoginDTO;
import org.example.passjava.member.dto.UmsUserRegisterDTO;
import org.example.passjava.member.service.AuthService;
import org.example.passjava.member.vo.LoginVO;
import org.example.passjava.member.vo.UmsUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "注册、登录、获取当前登录用户信息")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 注册
     * @param dto
     * @return
     */
    @PostMapping("/register")
    @Tag(name = "注册")
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
    @Tag(name = "登录")
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
    @Tag(name = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<UmsUserVO> me(@AuthenticationPrincipal Jwt jwt) {
        UmsUserVO umsUserVO = authService.me(jwt);
        return Result.success(umsUserVO,"获取用户信息成功");
    }
}
