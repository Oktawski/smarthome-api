package com.oktawski.iotserver.light;

import com.oktawski.iotserver.superclasses.IController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("lights")
public class LightController{

    private final LightService service;

    @Autowired
    public LightController(LightService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Light> add(@RequestBody Light light){
        return service.add(light);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Light> deleteById(@PathVariable("id") Long id){
        return service.deleteById(id);
    }

    @GetMapping
    public ResponseEntity<List<Light>> getAll(){
        return service.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Light> getById(@PathVariable("id") Long id){
        return service.getById(id);
    }

    @GetMapping("/ip/{ip}")
    public ResponseEntity<Light> getByIp(@PathVariable("ip") String ip){
        return service.getByIp(ip);
    }

    @PutMapping("{id}")
    public ResponseEntity<Light> update(@PathVariable("id") Long id, @RequestBody Light light){
        return service.update(id, light);
    }

    @PostMapping("{id}/turn")
    public ResponseEntity<Light> turnOnOf(@PathVariable("id") Long id){
        return service.turnOnOf(id);
    }

    @PostMapping("{id}/color")
    public ResponseEntity<Light> setColor(@PathVariable("id") Long id, short intensity, short[] rgb){
        service.setColor(id, intensity, rgb);
        return null;
    }
}
