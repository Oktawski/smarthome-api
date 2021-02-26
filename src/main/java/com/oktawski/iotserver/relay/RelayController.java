package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.IController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("relays")
public class RelayController implements IController<Relay> {

    private final RelayService service;

    @Autowired
    public RelayController(RelayService service){
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<BasicResponse<Relay>> add(@RequestBody @Valid Relay relay) {
        BasicResponse<Relay> response = service.add(relay);

        if(response.getT() == null){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping
    @Override
    public ResponseEntity<List<Relay>> getAll()
    {
        return service.getAll();
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Relay> getById(@PathVariable("id") Long relayId) {
        return service.getById(relayId);
    }

    @GetMapping("ip/{ip}")
    @Override
    public ResponseEntity<Relay> getByIp(@PathVariable("ip") String relayIp) {
        return service.getByIp(relayIp);
    }


    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Relay> turnOnOf(@PathVariable("id") Long relayId) {
        return service.turnOnOf(relayId);
    }

    @PutMapping("{id}")
    @Override
    public ResponseEntity<Relay> update(@PathVariable("id") Long relayId,
                                        @RequestBody @Valid Relay relay) {
        return service.update(relayId, relay);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        return service.deleteById(id);
    }
}
