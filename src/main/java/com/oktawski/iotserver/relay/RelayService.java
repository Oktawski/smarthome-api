package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.superclasses.IService;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelayService implements IService<Relay> {

    private final RelayRepository relayRepo;

    private final UserRepository userRepo;

    @Autowired
    public RelayService(@Qualifier("relayRepo") RelayRepository relayRepo,
                        @Qualifier("userRepo") UserRepository userRepo){
        this.relayRepo = relayRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ResponseEntity<Relay> add(Relay relay) {
        relayRepo.save(relay);

        if(relayRepo.exists(Example.of(relay))){
            return new ResponseEntity<>(relay, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Relay> deleteById(Long id) {
        Optional<Relay> relayOptional = relayRepo.findById(id);
        if(relayOptional.isPresent()){
            relayRepo.delete(relayOptional.get());
            if(!relayRepo.exists(Example.of(relayOptional.get()))) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<Relay>> getAll() {
        List<Relay> relays = relayRepo.findAll()
                .stream()
                .sorted(Comparator.comparing(Relay::getId))
                .collect(Collectors.toList());

        if(!relays.isEmpty()){
            return new ResponseEntity<>(relays, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> getById(Long id) {
        Optional<Relay> relayOptional = relayRepo.findById(id);
        if(relayOptional.isPresent()){
            return new ResponseEntity<>(relayOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> getByIp(String ip) {
        Relay relay = relayRepo.findRelayByIp(ip);
        if(relay != null){
            return new ResponseEntity<>(relay, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> update(Long id, Relay relay) {
        Optional<Relay> relayOptional =
                relayRepo.findById(id)
                .map(v -> {
                    v.setName(relay.getName());
                    v.setIp(relay.getIp());
                    v.setOn(relay.getOn());
                    return v;
                });

        relayRepo.save(relayOptional.get());

        if(relayRepo.exists(Example.of(relayOptional.get()))){
            //todo send data to ESP-8266
            return new ResponseEntity<>(relayOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Relay> turnOnOf(Long id) {
        Optional<Relay> relayOpt = relayRepo.findById(id);
        if(relayOpt.isPresent()){
            Relay relay = relayOpt.get();
            if(relay.getOn()){
                //todo send data to ESP-8266 to turn of relay
            }
            else{
                //todo send data to ESP-8266 to turn on relay
            }
            relay.turn();
            relayRepo.save(relay);

            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    //new things

    public ResponseEntity<?> addByUser(Long userId, Relay relay){
        Optional<User> userOpt = userRepo.findById(userId);
        if(userOpt.isPresent()){
            relay.setUser(userOpt.get());
            relayRepo.save(relay);


            return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getAllByUser(Long userId){
        List<Relay> relays = relayRepo.findByUserId(userId);
        if(!relays.isEmpty()) {
            return new ResponseEntity<>(relays, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> deleteById(Long userId, Long relayId) {
        Relay relay = find(userId, relayId);
        if(relay != null){
            relayRepo.delete(relay);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public Relay find(Long userId, Long relayId){
        User user = userRepo.getOne(userId);
        Relay relay = relayRepo.getOne(relayId);
        if(relay.getUser().equals(user)){
            return relay;
        }
        return null;
    }

}
