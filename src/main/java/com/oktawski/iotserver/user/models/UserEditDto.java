package com.oktawski.iotserver.user.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserEditDto extends UserDto {
    private String password;
}
