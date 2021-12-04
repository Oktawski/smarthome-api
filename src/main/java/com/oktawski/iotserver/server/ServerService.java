package com.oktawski.iotserver.server;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServerService {

    public String getStatus(){
        return "Server is up and running";
    }
}
