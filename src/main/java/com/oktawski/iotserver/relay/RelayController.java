package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.superclasses.IController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Relay> add(@RequestBody Relay relay) {
        return service.add(relay);
    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<Relay> deleteById(@PathVariable("id") Long id) {
        return service.deleteById(id);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<Relay>> getAll() {
        return service.getAll();
    }

    @GetMapping("{id}")
    @Override
    public ResponseEntity<Relay> getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @GetMapping("ip/{ip}")
    @Override
    public ResponseEntity<Relay> getByIp(@PathVariable("ip") String ip) {
        return service.getByIp(ip);
    }

    @PutMapping("{id}")
    @Override
    public ResponseEntity<Relay> update(@PathVariable("id") Long id, @RequestBody Relay relay) {
        return service.update(id, relay);
    }

    @PostMapping("{id}/turn")
    @Override
    public ResponseEntity<Relay> turnOnOf(@PathVariable("id") Long id) {
        return service.turnOnOf(id);
    }


    // BELOW ARE METHODS FOR USERS
    //new things
    //TODO get user from authorization instead of from path variable for all below!!!!

    @PostMapping("{user_id}/add")
    public ResponseEntity<?> addWithUser(@PathVariable("user_id") Long userId, @RequestBody Relay relay){
        return service.addByUser(relay, userId);
    }

    @DeleteMapping("{user_id}/{relay_id}")
    public ResponseEntity<?> deleteByUser(
            @PathVariable("user_id") Long userId,
            @PathVariable("relay_id") Long relayId){

        return service.deleteByUser(relayId, userId);
    }

    @GetMapping("{user_id}/get")
    public ResponseEntity<?> getAllByUser(@PathVariable("user_id") Long userId){
        return service.getAllByUser(userId);
    }

    @GetMapping("{user_id}/id/{relay_id}")
    public ResponseEntity<?> getOneById(
            @PathVariable("user_id") Long userId,
            @PathVariable("relay_id") Long relayId){

        return service.getOneById(relayId, userId);
    }

    @GetMapping("{user_id}/ip/{relay_ip}")
    public ResponseEntity<?> getOneByIp(
            @PathVariable("user_id") Long userId,
            @PathVariable("relay_ip") String relayIp){

        return service.getOneByIp(relayIp, userId);
    }

    @PostMapping("{user_id}/update/{relay_id}")
    public ResponseEntity<?> updateById(
            @PathVariable("user_id") Long userId,
            @PathVariable("relay_id") Long relayId,
            @RequestBody Relay relay){

        return service.updateById(relayId, userId, relay);
    }

    @PostMapping("{user_id}/turn/{relay_id}")
    public ResponseEntity<?> turnByUser(
            @PathVariable("user_id") Long userId,
            @PathVariable("relay_id") Long relayId){

        return service.turnByUser(relayId, userId);
    }
}
