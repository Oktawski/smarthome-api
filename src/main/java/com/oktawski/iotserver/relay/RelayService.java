package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.jwt.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Autowired
    public RelayService(@Qualifier("relayRepo") RelayRepository relayRepo,
                        @Qualifier("userRepo") UserRepository userRepo,
                        JwtUtil jwtUtil){
        this.relayRepo = relayRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<Relay> add(String token, Relay relay) {

        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        userOpt.ifPresent(v -> {
            relay.setUser(v);
            relayRepo.save(relay);
        });

        if(relayRepo.findOne(Example.of(relay)).isPresent()){
            return new ResponseEntity<>(relay, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> deleteById(String token, Long relayId) {
        String username = jwtUtil.getUsername(token);
        Optional<Relay> relayOpt = relayRepo.findById(relayId);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        relayOpt.ifPresent(v -> {
            if(v.getUser().getId() == userOpt.get().getId()){
                relayRepo.delete(v);
            }
        });

        if(relayRepo.findById(relayId).isEmpty()){
            return new ResponseEntity<>("Relay  removed", HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<Relay>> getAll(String token) {
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList().stream()
                    .sorted(Comparator.comparing(Relay::getId))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(relays, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> getById(String token, Long relayId) {
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList();
            Optional<Relay> relay = relays.stream()
                    .filter(v -> v.getId() == relayId)
                    .findFirst();

            relay.ifPresentOrElse(
                    v -> new ResponseEntity<>(v, HttpStatus.OK),
                    () -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND)
            );
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> getByIp(String token, String relayIp) {
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);
        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList();
            Optional<Relay> relay = relays.stream()
                    .filter(v -> v.getIp().equals(relayIp))
                    .findFirst();

            if(relay.isPresent()){
                return new ResponseEntity<>(relay.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> update(String token, Long relayId, Relay relay) {
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()) {
            Optional<Relay> relayOpt = findRelay(relayId, userOpt.get().getId())
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
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Relay> turnOnOf(String token, Long relayId) {
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            Optional<Relay> relayOpt = findRelay(relayId, userOpt.get().getId());

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
