package com.oktawski.iotserver.light;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("lights")
public class LightController {

    private final LightService service;

    @Autowired
    public LightController(LightService service){
        this.service = service;
    }

    @PostMapping("add")
    public ResponseEntity<Light> addLight(@RequestBody Light light){
        return service.add(light);
    }

    @PostMapping("{id}/remove")
    public ResponseEntity<Light> removeLight(@PathVariable("id") Long id){
        return service.deleteById(id);
    }

    @GetMapping()
    public ResponseEntity<List<Light>> getAllLights(){
        return service.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Light> getLightById(@PathVariable("id") Long id){
        return service.getById(id);
    }

    @GetMapping("/ip/{ip}")
    public ResponseEntity<Light> getLightByIp(@PathVariable("ip") String ip){
        return service.getByIp(ip);
    }

    @PostMapping("{id}")
    public ResponseEntity<Light> updateLight(@PathVariable("id") Long id, @RequestBody Light light){
        return service.update(id, light);
    }

    @PostMapping("{id}/turn")
    public ResponseEntity<Light> turnLight(@PathVariable("id") Long id){
        return service.turnOnOf(id);
    }

    @PostMapping("{id}/color")
    public ResponseEntity<Light> setColor(@PathVariable("id") Long id, short intensity, short[] rgb){
        service.setColor(id, intensity, rgb);
        return null;
    }
}
