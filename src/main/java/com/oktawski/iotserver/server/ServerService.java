package com.oktawski.iotserver.server;

import com.oktawski.iotserver.common.SimpleResult;
import org.springframework.stereotype.Service;

import com.oktawski.iotserver.common.Status;
import com.oktawski.iotserver.identity.CurrentUserProvider;

@Service
public class ServerService {

    private final CurrentUserProvider currentUserProvider;

    public ServerService(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public SimpleResult getStatus(){
        var currentUser = currentUserProvider.getCurrentUser();

        System.out.println();

        return SimpleResult.success("Server up and running\n" + "Requested by: " + currentUser.getUsername());
    }
}
