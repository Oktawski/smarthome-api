package com.oktawski.iotserver.user.models;

public class LoginResponse extends BasicResponse<User> {

    public LoginResponse() {
    }

    public LoginResponse(User user, String msg) {
        super(user, msg);
    }
}
