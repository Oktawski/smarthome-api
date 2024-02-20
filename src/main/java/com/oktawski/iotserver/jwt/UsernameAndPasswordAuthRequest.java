package com.oktawski.iotserver.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsernameAndPasswordAuthRequest {
    private String username;
    private String password;

    public UsernameAndPasswordAuthRequest() {
    }
}
