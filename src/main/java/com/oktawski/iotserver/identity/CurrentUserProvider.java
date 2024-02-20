package com.oktawski.iotserver.identity;

import com.oktawski.iotserver.user.models.User;

public interface CurrentUserProvider {
    User getCurrentUser();
}
