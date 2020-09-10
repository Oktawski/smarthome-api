package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.superclasses.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelayService implements IService<Relay> {

    private final RelayRepository repository;

    @Autowired
    public RelayService(@Qualifier("relayRepository") RelayRepository repository){
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Relay> add(Relay relay) {
        repository.save(relay);

        if(repository.exists(Example.of(relay))){
            return new ResponseEntity<>(relay, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Relay> deleteById(Long id) {
        Optional<Relay> relayOptional = repository.findById(id);
        repository.delete(relayOptional.get());
        if(!repository.exists(Example.of(relayOptional.get()))){
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<Relay>> getAll() {
        List<Relay> relays = repository.findAll();
        if(!relays.isEmpty()){
            return new ResponseEntity<>(relays, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> getById(Long id) {
        Optional<Relay> relayOptional = repository.findById(id);
        if(relayOptional.isPresent()){
            return new ResponseEntity<>(relayOptional.get(), HttpStatus.FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> update(Long id, Relay relay) {
        Optional<Relay> relayOptional =
                repository.findById(id)
                .map(v -> {
                    v.setName(relay.getName());
                    v.setIp(relay.getIp());
                    v.setOn(relay.getOn());
                    return v;
                });

        repository.save(relayOptional.get());

        if(repository.exists(Example.of(relayOptional.get()))){
            //todo send data to ESP-8266
            return new ResponseEntity<>(relayOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Relay> turnOnOf(Long id) {
        Optional<Relay> relayOpt = repository.findById(id);
        if(relayOpt.isPresent()){
            Relay relay = relayOpt.get();
            if(relay.getOn()){
                //todo send data to ESP-8266 to turn of relay
            }
            else{
                //todo send data to ESP-8266 to turn on relay
            }
            relay.turn();
            repository.save(relay);

            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

    }
}
