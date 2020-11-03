package com.oktawski.iotserver.responses;

import com.oktawski.iotserver.user.models.User;

public class UserResponse extends BasicResponse<User> {

    public UserResponse() {
    }

    public UserResponse(User user, String msg) {
        super(user, msg);
    }
}
