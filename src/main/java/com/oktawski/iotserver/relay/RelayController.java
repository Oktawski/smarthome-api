package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.IController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("relays")
public class RelayController implements IController<Relay> {

    private final RelayService service;

    @Autowired
    public RelayController(RelayService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<BasicResponse<Relay>> add(@RequestBody @Valid Relay relay) {
        BasicResponse<Relay> response = service.add(relay);

        if (response.getT() == null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        Optional<Relay> relayOpt = service.deleteById(id);

        return relayOpt.map(v -> new ResponseEntity<>("Could not remove relay", HttpStatus.BAD_REQUEST))
                .orElse(new ResponseEntity<>("Relay removed", HttpStatus.OK));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Relay>> getAll() {
        Optional<List<Relay>> relays = service.getAll();

        return relays.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Relay> getById(@PathVariable("id") Long id) {
        Optional<Relay> relayOpt = service.getById(id);

        return relayOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("ip/{ip}")
    @Override
    public ResponseEntity<Relay> getByIp(@PathVariable("ip") String ip) {
        Optional<Relay> relayOpt = service.getByIp(ip);

        return relayOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }


    @PutMapping("{id}")
    @Override
    public ResponseEntity<Relay> update(@PathVariable("id") Long relayId,
                                        @RequestBody @Valid Relay relay) {
        Optional<Relay> relayOpt = service.update(relayId, relay);

        return relayOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }


    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Relay> turnOnOf(@PathVariable("id") Long relayId) {
        Optional<Relay> relayOpt = service.turnOnOf(relayId);

        return relayOpt.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }
}