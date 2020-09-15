package com.oktawski.iotserver.user.models;

public class SignupResponse extends BasicResponse<User> {

    public SignupResponse() {
    }

    public SignupResponse(User user, String msg) {
        super(user, msg);
    }
}
