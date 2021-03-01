package com.oktawski.iotserver.light;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("lights")
public class LightController{

    private final LightService service;

    @Autowired
    public LightController(LightService service){
        this.service = service;
    }


}
