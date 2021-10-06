package com.oktawski.iotserver.devices.relay;

import com.oktawski.iotserver.jwt.JwtUtil;
import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.user.models.User;
import com.oktawski.iotserver.utilities.ServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import com.oktawski.iotserver.superclasses.ServiceInterface;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RelayService implements ServiceInterface<Relay> {

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
    public BasicResponse<Relay> add(Relay relay) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        var response = new BasicResponse<Relay>();

        userOpt.ifPresent(v -> {
            var relays = userOpt.get().getRelayList();
            if(!serviceHelper.isIpUnique(relays, relay.getIp())){
                relay.setUser(v);
                relayRepo.save(relay);

                response.setObject(relay);
                response.setMsg("Relay added");

                //turn(relay);
            }
            else{
                response.setObject(null);
                response.setMsg(String.format("Relay with ip: %s already exists", relay.getIp()));
            }
        });
        return response;
    }

    @Override
    public Optional<Relay> deleteById(Long relayId) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);
        var relayOpt = relayRepo.findById(relayId);

        relayOpt.ifPresent(relay -> {
            userOpt.ifPresent(user -> {
                if(relay.getUser().getId().equals(user.getId())){
                    relayRepo.delete(relay);
                }
            });
        });

        if(relayRepo.findById(relayId).isEmpty()){
            return Optional.empty();
        }
        return relayOpt;
    }

    @Override
    public Optional<List<Relay>> getAll() {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(v -> {
            List<Relay> relays = v.getRelayList().stream()
                    .sorted(Comparator.comparing(Relay::getId))
                    .collect(Collectors.toList());
            return Optional.of(relays);
        }).orElse(Optional.empty());
    }

    @Override
    public Optional<Relay> getById(Long id) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            return userOpt.map(v -> Optional.of(v.getRelayById(id)))
                    .orElse(null);
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Relay>getByIp(String ip) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(v -> Optional.of(v.getRelayByIp(ip)))
                .orElse(null);
    }



    @Override
    public Optional<Relay> update(Long id, Relay relay) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            var relayOpt = userOpt.map(v -> v.getRelayById(id));
            relayOpt.map(v -> {
                v.setName(relay.getName());
                v.setIp(relay.getIp());
                v.setOn(relay.getOn());
                return v;
            });

            relayOpt.ifPresent(relayRepo::save);

            if(relayRepo.existsById(id)){
                return relayOpt;
            }
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Relay> turnOnOff(Long relayId) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            var relayOpt = userOpt.map(v -> v.getRelayById(relayId));
            relayOpt.map(v -> {
                v.turnOnOff();
                relayRepo.save(v);
                return v;
            });

            if (relayRepo.existsById(relayId)) {
                turn(relayOpt.get());
                return relayOpt;
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    // TODO test with ESP-01 module
/*    @Async
    protected void turnOnOff(Relay relay) {
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
    }*/
}
