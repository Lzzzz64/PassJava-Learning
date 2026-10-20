
package org.example.passjava.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.passjava.question.dto.UmsUserLoginDTO;
import org.example.passjava.question.dto.UmsUserRegisterDTO;
import org.example.passjava.question.entity.UmsUser;
import org.example.passjava.question.mapper.UmsUserMapper;
import org.example.passjava.question.service.AuthService;
import org.example.passjava.question.service.JwtService;
import org.example.passjava.question.vo.LoginVO;
import org.example.passjava.question.vo.UmsUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UmsUserMapper umsUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    public Boolean register(UmsUserRegisterDTO dto) {
        UmsUser existing = umsUserMapper.selectOne(new QueryWrapper<UmsUser>().eq("username", dto.getUsername()));

        if (existing != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "用户名已存在");
        }

        UmsUser user = new UmsUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRole("USER");
        user.setStatus(0);

        try {
            int insert = umsUserMapper.insert(user);
            return insert > 0;
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "用户名已存在");
        }
    }

    public LoginVO login(UmsUserLoginDTO dto) {
        UmsUser user = umsUserMapper.selectOne(new QueryWrapper<UmsUser>().eq("username", dto.getUsername()));

        if (user == null ||
                !passwordEncoder.matches(
                        dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        return new LoginVO(jwtService.generateToken(user), "Bearer");
    }

    @Override
    public UmsUserVO me(Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        UmsUser user = umsUserMapper.selectById(userId);
        if(user == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "用户不存在或已禁用");
        UmsUserVO umsUserVO = new UmsUserVO();
        umsUserVO.setId(user.getId());
        umsUserVO.setUsername(user.getUsername());
        umsUserVO.setNickname(user.getNickname());
        umsUserVO.setRole(user.getRole());
        return umsUserVO;
    }
}
