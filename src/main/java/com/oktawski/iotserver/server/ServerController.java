package com.oktawski.iotserver.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.channels.NotYetConnectedException;

@Controller
@RequestMapping("server")
public class ServerController {

    private final ServerService service;

    @Autowired
    public ServerController(ServerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<String> getStatus(){
        try {
            String status = service.getStatus().orElseThrow(NotYetConnectedException::new);
            return new ResponseEntity<>(status, HttpStatus.OK);
        }
        catch(NotYetConnectedException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
