package org.example.passjava.member.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {

    private String accessToken;
    private String tokenType;
}