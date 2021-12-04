package com.oktawski.iotserver.relay;

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
@RequestMapping("relays")
public class RelayController implements IController<Relay> {

    private final RelayService service;

    @Autowired
    public RelayController(RelayService service) {
        this.service = service;
    }

    @PostMapping("init")
    public ResponseEntity<BasicResponse<Relay>> initRelay(@RequestBody InitDeviceRequest request) {
        BasicResponse<Relay> response = service.initDevice(request.mac, request.ip);
        if (response.getObject() == null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @Override
    public ResponseEntity<BasicResponse<Relay>> add(@RequestBody @Valid Relay relay) {
        BasicResponse<Relay> response = service.add(relay);

        if (response.getObject() != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        Optional<Relay> relayOpt = service.deleteByIdForUser(id);

        return relayOpt.map(v -> new ResponseEntity<>("Relay removed", HttpStatus.OK))
                .orElse(new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Relay>> getAll() {
        var relaysOpt = service.getAll();

        return relaysOpt.map(relays -> new ResponseEntity<>(relays, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Relay> getById(@PathVariable("id") Long id) {
        var relayOpt = service.getById(id);

        return relayOpt.map(mRelay -> new ResponseEntity<>(mRelay, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    @Override
    public ResponseEntity<Relay> update(@PathVariable("id") Long relayId,
                                        @RequestBody @Valid Relay relay) {
        var relayOpt = service.update(relayId, relay);

        return relayOpt.map(mRelay -> new ResponseEntity<>(mRelay, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }


    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Relay> turnOnOf(@PathVariable("id") Long relayId) {
        var relayOpt = service.turnOnOf(relayId);

        return relayOpt.map(mRelay -> new ResponseEntity<>(mRelay, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }
}