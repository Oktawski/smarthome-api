package com.oktawski.iotserver.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserDto {
    private Long id;

    private String username;

    @Email
    private String email;
}
