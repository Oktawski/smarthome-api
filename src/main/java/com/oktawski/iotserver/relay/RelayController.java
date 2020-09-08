package com.oktawski.iotserver.relay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller("relays ")
public class RelayController{

    private final RelayService service;

    @Autowired
    public RelayController(RelayService service){
        this.service = service;
    }





}
