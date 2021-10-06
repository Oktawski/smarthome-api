package com.oktawski.iotserver.devices.thermometer;

import com.oktawski.iotserver.responses.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import com.oktawski.iotserver.superclasses.ControllerInterface;

@Controller
@RequestMapping("thermometers")
public class ThermometerController implements ControllerInterface<Thermometer> {

    private final ThermometerService service;

    @Autowired
    public ThermometerController(ThermometerService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<BasicResponse<Thermometer>> add(@RequestBody @Valid Thermometer thermometer) {
        BasicResponse<Thermometer> response = service.add(thermometer);

        if (response.getT() == null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        Optional<Thermometer> thermometerOpt = service.deleteById(id);

        return thermometerOpt.map(v -> new ResponseEntity<>("Could not remove thermometer", HttpStatus.BAD_REQUEST))
                .orElse(new ResponseEntity<>("Thermometer removed", HttpStatus.OK));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Thermometer>> getAll() {
        var thermometers = service.getAll();

        return thermometers.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Thermometer> getById(@PathVariable("id") Long id) {
        var thermometerOpt = service.getById(id);

        return thermometerOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("ip/{ip}")
    @Override
    public ResponseEntity<Thermometer> getByIp(@PathVariable("ip") String ip) {
        var  thermometerOpt = service.getByIp(ip);

        return thermometerOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }


    @PutMapping("{id}")
    @Override
    public ResponseEntity<Thermometer> update(@PathVariable("id") Long thermometerId,
                                        @RequestBody @Valid Thermometer thermometer) {
        var thermometerOpt = service.update(thermometerId, thermometer);

        return thermometerOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }


    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Thermometer> turnOnOf(@PathVariable("id") Long thermometerId) {
        var thermometerOpt = service.turnOnOf(thermometerId);

        return thermometerOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }
}