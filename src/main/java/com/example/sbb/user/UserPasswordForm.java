package com.example.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserPasswordForm {
    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;

    @NotEmpty(message = "아이디는 필수입니다.")
    private String username;
}
