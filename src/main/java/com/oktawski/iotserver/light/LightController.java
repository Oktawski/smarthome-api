package com.oktawski.iotserver.light;

import com.oktawski.iotserver.responses.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("lights")
public class LightController{

    private final LightService service;

    private final static String AUTH_HEADER = "Authorization";

    @Autowired
    public LightController(LightService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Light>> getAll(@RequestHeader(AUTH_HEADER) String token) {
        return service.getAll(token);
    }

    @GetMapping("{id}")
    public ResponseEntity<Light> getById(@RequestHeader(AUTH_HEADER) String token,
                                         @PathVariable("id") Long id){
        return service.getById(token, id);
    }

    @GetMapping("/ip/{ip}")
    public ResponseEntity<Light> getByIp(@RequestHeader(AUTH_HEADER) String token,
                                         @PathVariable("ip") String ip){
        return service.getByIp(token, ip);
    }

    @PostMapping
    public ResponseEntity<BasicResponse<?>> add(@RequestHeader(AUTH_HEADER) String token,
                                             @RequestBody Light light){
        return service.add(token, light);
    }

    @PostMapping("{id}/turn")
    public ResponseEntity<Light> turnOnOf(@RequestHeader(AUTH_HEADER) String token,
                                          @PathVariable("id") Long id){
        return service.turnOnOf(token, id);
    }

    @PostMapping("{id}/color")
    public ResponseEntity<Light> setColor(@RequestHeader(AUTH_HEADER) String token,
                                          @PathVariable("id") Long id, short intensity, short[] rgb){
        service.setColor(id, intensity, rgb);
        return null;
    }

    @PutMapping("{id}")
    public ResponseEntity<Light> update(@RequestHeader(AUTH_HEADER) String token,
                                        @PathVariable("id") Long id, @RequestBody Light light){
        return service.update(token, id, light);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Light> deleteById(@RequestHeader(AUTH_HEADER) String token,
                                            @PathVariable("id") Long id){
        return service.deleteById(id);
    }
}
