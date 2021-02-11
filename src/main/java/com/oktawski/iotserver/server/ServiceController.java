package com.oktawski.iotserver.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("server")
public class ServiceController {

    private final ServerService service;

    @Autowired
    public ServiceController(ServerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<String> getStatus(){
        return service.getStatus();
    }
}
