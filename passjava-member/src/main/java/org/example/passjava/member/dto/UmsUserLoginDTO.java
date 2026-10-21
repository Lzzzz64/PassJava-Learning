package org.example.passjava.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UmsUserLoginDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
