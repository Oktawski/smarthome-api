package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.jwt.JwtUtil;
import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.IService;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.user.models.User;
import com.oktawski.iotserver.utilities.ServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RelayService implements IService<Relay> {

    private final RelayRepository relayRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    private final ServiceHelper serviceHelper;

    @Autowired
    public RelayService(@Qualifier("relayRepo") RelayRepository relayRepo,
                        @Qualifier("userRepo") UserRepository userRepo,
                        JwtUtil jwtUtil,
                        ServiceHelper serviceHelper){
        this.relayRepo = relayRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.serviceHelper = serviceHelper;
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
            return relays.stream()
                    .filter(v -> v.getId().equals(relayId))
                    .findFirst()
                    .map(ResponseEntity::ok)    //if found return ResponseEntity<>(relay, OK)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Relay> getByIp(String token, String relayIp) {
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);
        if(userOpt.isPresent()){
            List<Relay> relays = userOpt.get().getRelayList();
            return relays.stream()
                    .filter(v -> v.getIp().equals(relayIp))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<BasicResponse<Relay>> add(String token, Relay relay) {

        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        AtomicReference<HttpStatus> httpStatus = new AtomicReference<>();
        BasicResponse<Relay> response = new BasicResponse<>();

        userOpt.ifPresent(v -> {
            List<Relay> relays = userOpt.get().getRelayList();
            if(!serviceHelper.isIpUnique(relays, relay.getIp())){
                relay.setUser(v);
                relayRepo.save(relay);

                httpStatus.set(HttpStatus.OK);
                response.setObject(relay);
                response.setMsg("Relay added");

                turn(relay);
            }
            else{
                httpStatus.set(HttpStatus.BAD_REQUEST);
                response.setObject(null);
                response.setMsg(String.format("Relay with ip: %s already exists", relay.getIp()));
            }
        });

        return new ResponseEntity<>(response, httpStatus.get());
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

            relayOpt.ifPresent(relayRepo::save);

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

                turn(relay);

                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }



    @Override
    public ResponseEntity<?> deleteById(String token, Long relayId) {
        String username = jwtUtil.getUsername(token);
        Optional<Relay> relayOpt = relayRepo.findById(relayId);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        relayOpt.ifPresent(v -> {
            userOpt.ifPresent(b -> {
                if(v.getUser().getId().equals(b.getId())){
                    relayRepo.delete(v);
                }
            });
        });

        if(relayRepo.findById(relayId).isEmpty()){
            return new ResponseEntity<>("Relay  removed", HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    private Optional<Relay> findRelay(Long relayId, Long userId){
        User user = userRepo.getOne(userId);
        return user.getRelayList().stream()
                .filter(v -> v.getId().equals(relayId))
                .findFirst();
    }

    // TODO test with ESP-01 module
    @Async
    protected void turn(Relay relay) {
        try {
            System.out.println("Turn starts");  //temp

            String ip = relay.getIp();
            byte[] data = relay.getOn().toString().getBytes(StandardCharsets.UTF_8);
            Socket socket = new Socket(ip, 80);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);

            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(Arrays.toString(data));

            System.out.println("Turn done");    //temp
        }
        catch (UnknownHostException e){
            System.out.printf("Unknown IP: %s%n", e.getMessage());
        }
        catch (IOException e){
            System.out.println(e.toString());
        }
    }
}
