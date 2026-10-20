package org.example.passjava.question.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.example.passjava.question.dto.UmsUserLoginDTO;
import org.example.passjava.question.dto.UmsUserRegisterDTO;
import org.example.passjava.question.vo.LoginVO;
import org.example.passjava.question.vo.UmsUserVO;

public interface AuthService {
    LoginVO login(UmsUserLoginDTO dto);
    Boolean register(UmsUserRegisterDTO dto);
    UmsUserVO me(Jwt jwt);
}
