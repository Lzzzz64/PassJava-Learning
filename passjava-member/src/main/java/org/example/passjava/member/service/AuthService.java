package org.example.passjava.member.service;

import org.example.passjava.member.dto.UmsUserRegisterDTO;
import org.example.passjava.member.vo.UmsUserVO;
import org.example.passjava.member.dto.UmsUserLoginDTO;
import org.example.passjava.member.vo.LoginVO;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthService {
    LoginVO login(UmsUserLoginDTO dto);
    Boolean register(UmsUserRegisterDTO dto);
    UmsUserVO me(Jwt jwt);
}
