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

    private final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping
    public ResponseEntity<String> getStatus() {
        try {
            var result = serverService.getStatus();

            return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
        } catch(NotYetConnectedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
