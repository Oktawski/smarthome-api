package com.oktawski.iotserver.server;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServerService {

    public ResponseEntity<String> getStatus(){
        return ResponseEntity.ok("Server is on");
    }
}
