package org.example.passjava.question.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {

    private String accessToken;
    private String tokenType;
}