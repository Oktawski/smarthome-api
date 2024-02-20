package com.oktawski.iotserver.user.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;
}
