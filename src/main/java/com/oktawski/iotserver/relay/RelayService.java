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
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RelayService implements IService<Relay> {

    private final RelayRepository relayRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    private final ServiceHelper serviceHelper;

    @Autowired
    public RelayService(@Qualifier("relayRepository") RelayRepository relayRepo,
                        @Qualifier("userRepository") UserRepository userRepo,
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
        }).orElse(empty());
    }

    @Override
    public Optional<Relay> getById(Long id) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(user -> getRelayByIdAndUserId(id, user.getId()))
                .orElse(null);
    }

    @Override
    public Optional<Relay> update(Long id, Relay relay) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        var relayOpt = userOpt.map(v -> getRelayByIdAndUserId(id, v.getId()).orElse(null));
        relayOpt.ifPresent(relay1 -> {
            relay1.setName(relay.getName());
            relay1.setOn(relay.getOn());
            relayRepo.save(relay1);
        });

        return relayRepo.findOne(Example.of(relay));
    }

    @Override
    public Optional<Relay> turnOnOf(Long relayId) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(user -> {
            var relay = getRelayByIdAndUserId(relayId, user.getId());
            return relay.map(v -> {
                v.turn();
                relayRepo.save(v);
                turn(v);
                return v;
            }).orElse(null);

        });
    }

    private Optional<Relay> getRelayByIdAndUserId(Long relayId, Long userId) {
        return relayRepo.findRelaysByUserId(userId)
                .stream().filter(relay1 -> relay1.getId().equals(relayId)).findFirst();
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
        catch (UnknownHostException e) {
            System.out.printf("Unknown IP: %s%n", e.getMessage());
        }
        catch (NoRouteToHostException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
