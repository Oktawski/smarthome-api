package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.superclasses.IController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO get userId or username from header and pass to service
@Controller
@RequestMapping("relays")
public class RelayController implements IController<Relay> {

    private final RelayService service;

    private final static String AUTH_HEADER = "Authorization";

    @Autowired
    public RelayController(RelayService service){
        this.service = service;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Relay>> getAll(@RequestHeader(AUTH_HEADER) String token)
    {
        return service.getAll(token);
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Relay> getById(@RequestHeader(AUTH_HEADER) String token,
                                         @PathVariable("id") Long relayId) {
        return service.getById(token, relayId);
    }

    @GetMapping("ip/{ip}")
    @Override
    public ResponseEntity<Relay> getByIp(@RequestHeader(AUTH_HEADER) String token,
                                         @PathVariable("ip") String relayIp) {
        return service.getByIp(token, relayIp);
    }

    @PostMapping
    @Override
    public ResponseEntity<Relay> add(@RequestHeader(AUTH_HEADER) String token,
                                     @RequestBody Relay relay)
    {
        return service.add(token, relay);
    }

    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Relay> turnOnOf(@RequestHeader(AUTH_HEADER) String token,
                                          @PathVariable("id") Long relayId) {
        return service.turnOnOf(token, relayId);
    }

    @PutMapping("{id}")
    @Override
    public ResponseEntity<Relay> update(@RequestHeader(AUTH_HEADER) String token,
                                        @PathVariable("id") Long relayId,
                                        @RequestBody Relay relay) {
        return service.update(token, relayId, relay);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<?> deleteById(@RequestHeader(AUTH_HEADER) String token,
                                            @PathVariable("id") Long relayId) {

        return service.deleteById(token, relayId);
    }
}
