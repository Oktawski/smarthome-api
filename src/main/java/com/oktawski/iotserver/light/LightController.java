package com.oktawski.iotserver.light;

import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.IController;
import com.oktawski.iotserver.utilities.InitDeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("lights")
public class LightController implements IController<Light> {

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

    @PostMapping
    @Override
    public ResponseEntity<BasicResponse<Light>> add(@RequestBody @Valid Light light) {
        BasicResponse<Light> response = service.add(light);

        if (response.getObject() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        Optional<Light> lightOpt = service.deleteByIdForUser(id);

        return lightOpt.map(light -> new ResponseEntity<>("Light removed", HttpStatus.OK))
                .orElse(new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Light>> getAll() {
        var lightsOpt = service.getAll();

        return lightsOpt.map(lights -> new ResponseEntity<>(lights, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Light> getById(@PathVariable("id") Long id) {
        var lightOpt = service.getById(id);

        return lightOpt.map(light -> new ResponseEntity<>(light, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    @Override
    public ResponseEntity<Light> update(@PathVariable("id") Long id,
                                        @RequestBody Light light) {
        var lightOpt = service.update(id, light);

        return lightOpt.map(mLight -> new ResponseEntity<>(mLight, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Light> turnOnOf(@PathVariable("id") Long id) {
        var lightOpt = service.turnOnOf(id);

        return lightOpt.map(light -> new ResponseEntity<>(light, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

}
