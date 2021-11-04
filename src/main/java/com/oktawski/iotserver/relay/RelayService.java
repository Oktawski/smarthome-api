package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.jwt.JwtUtil;
import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.IService;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.utilities.ServiceHelper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
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

    public BasicResponse<Relay> initRelay(String mac, String ip) {
        var relay = relayRepo.findRelayByMac(mac);
        if(relay != null) {
            relay.setIp(ip);
            System.out.println("Relay exists");
            System.out.println(relay.getMac());
            return new BasicResponse<>(relay, "Relay exists");
        } else {
            var newRelay = new Relay();
            newRelay.setMac(mac);
            newRelay.setIp(ip);
            System.out.println("New relay");
            System.out.println(newRelay.getMac());
            relayRepo.save(newRelay);
            return new BasicResponse<>(newRelay, "New relay registered");
        }

    }

    @Override
    public BasicResponse<Relay> add(Relay relay) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        var response = new BasicResponse<Relay>();

        userOpt.ifPresent(v -> {
            var relayByMac = relayRepo.findRelayByMac(relay.getMac());
            if (relayByMac != null) {
                relayByMac.setUser(v);
                relayByMac.setName(relay.getName());
                relayRepo.save(relayByMac);
                response.setObject(relayByMac);
                response.setMsg("Relay added");
            } else {
                response.setObject(null);
                response.setMsg("Relay with such MAC does not exist");
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
                    relay.setUser(null);
                    relayRepo.save(relay);
                    //relayRepo.delete(relay);
                }
            });
        });

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

    // Todo remove
    @Override
    public Optional<Relay>getByIp(String ip) {
        /*var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(v -> Optional.of(v.getRelayByIp(ip)))
                .orElse(null);*/

        return Optional.ofNullable(relayRepo.findRelayByIp(ip));
    }



    @Override
    public Optional<Relay> update(Long id, Relay relay) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            var relayOpt = userOpt.map(v -> v.getRelayById(id));
            relayOpt.map(v -> {
                v.setName(relay.getName());
                //v.setIp(relay.getIp());
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
    public Optional<Relay> turnOnOf(Long relayId) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            var relayOpt = userOpt.map(v -> v.getRelayById(relayId));
            relayOpt.map(v -> {
                v.turn();
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

    @Async
    protected void turn(Relay relay) {
        try {
            URL url = new URL("http://" + relay.getIp() + ":80/");
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url.toString());
            JSONObject json = new JSONObject();
            json.put("on", relay.getOn());
            StringEntity stringEntity = new StringEntity(json.toString());
            request.setEntity(stringEntity);
            request.addHeader("content-type", "application/json");
            client.execute(request);
        }
        catch (UnknownHostException e){
            System.out.printf("Unknown IP: %s%n", e.getMessage());
        }
        catch (IOException e){
            System.out.println(e);
        }
    }
}
