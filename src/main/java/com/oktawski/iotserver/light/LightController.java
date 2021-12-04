package com.oktawski.iotserver.light;

import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.utilities.InitDeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("lights")
public class LightController{

    private final LightService service;

    @Autowired
    public LightController(LightService service){
        this.service = service;
    }

    @PostMapping("init")
    public ResponseEntity<BasicResponse<Light>> initDevice(@RequestBody InitDeviceRequest request) {
        BasicResponse<Light> response = service.initDevice(request.mac, request.ip);
        if (response.getObject() == null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
