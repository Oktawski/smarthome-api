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

import java.util.ArrayList;
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
        Optional<Relay> relayOpt = relayRepo.findById(id);

        relayOpt.ifPresent(v -> {
            relayRepo.delete(v);
        });

        if(!relayRepo.existsById(id)){
            return new ResponseEntity<>(relayOpt.get(), HttpStatus.OK);
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

        relayOptional.ifPresent(relayRepo::save);

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

    public ResponseEntity<Relay> addByUser(Relay relay, Long userId){
        Optional<User> userOpt = userRepo.findById(userId);

        userOpt.ifPresent(v -> {
            relay.setUser(v);
            relayRepo.save(relay);
        });

        if(relayRepo.findOne(Example.of(relay)).isPresent()){
            return new ResponseEntity<>(relay, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> deleteByUser(Long relayId, Long userId){
        Optional<Relay> relayOpt = relayRepo.findById(relayId);
        relayOpt.ifPresent(v -> {
            if(v.getUser().getId() == userId){
                relayRepo.delete(v);
            }
        });

        if(relayRepo.findById(relayId).isEmpty()){
            return new ResponseEntity<>("Relay  removed", HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getAllByUser(Long userId){
        Optional<User> userOpt = userRepo.findById(userId);
        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList().stream()
                    .sorted(Comparator.comparing(Relay::getId))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(relays, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getOneById(Long relayId, Long userId){
        Optional<User> userOpt = userRepo.findById(userId);
        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList();
            Optional<Relay> relay = relays.stream()
                    .filter(v -> v.getId() == relayId)
                    .findFirst();

            if(relay.isPresent()){
                return new ResponseEntity<>(relay, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getOneByIp(String relayIp, Long userId){
        Optional<User> userOpt = userRepo.findById(userId);
        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList();
            Optional<Relay> relay = relays.stream()
                    .filter(v -> v.getIp().equals(relayIp))
                    .findFirst();

            if(relay.isPresent()){
                return new ResponseEntity<>(relay, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateById(Long relayId, Long userId, Relay relay){
        Optional<Relay> relayOpt = findRelay(relayId, userId)
                .map(v -> {
                    v.setName(relay.getName());
                    v.setIp(relay.getIp());
                    v.setOn(relay.getOn());
                    return v;
                });

        relayRepo.save(relayOpt.get());

        if(relayRepo.exists(Example.of(relayOpt.get()))){
            return new ResponseEntity<>(relayOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> turnByUser(Long relayId, Long userId) {
        Optional<Relay> relayOpt = findRelay(relayId, userId);

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

    private Optional<Relay> findRelay(Long relayId, Long userId){
        User user = userRepo.getOne(userId);
        return user.getRelayList().stream()
                .filter(v -> v.getId() == relayId)
                .findFirst();
    }
}
