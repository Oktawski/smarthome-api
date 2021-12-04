package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.jwt.JwtUtil;
import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.DeviceService;
import com.oktawski.iotserver.superclasses.IService;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.utilities.ServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RelayService extends DeviceService<Relay> implements IService<Relay> {

    private final RelayRepository relayRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final ServiceHelper serviceHelper;

    @Autowired
    public RelayService(@Qualifier("relayRepository") RelayRepository relayRepository,
                        @Qualifier("userRepository") UserRepository userRepository,
                        JwtUtil jwtUtil,
                        ServiceHelper serviceHelper){
        super(relayRepository);
        this.relayRepository = relayRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.serviceHelper = serviceHelper;
    }

    @Override
    public BasicResponse<Relay> initDevice(String mac, String ip) {
        var relay = relayRepository.findRelayByMac(mac);
        relay = registerOrUpdateDevice(Objects.requireNonNullElseGet(relay, Relay::new), mac, ip);
        return new BasicResponse<>(relay, "Relay registered");
    }

    @Override
    public BasicResponse<Relay> add(Relay relay) {
        var user = getUser(userRepository);

        var response = new BasicResponse<Relay>();

        var relayByMac = relayRepository.findRelayByMac(relay.getMac());
        if (relayByMac != null) {
            relayByMac.setUser(user);
            relayByMac.setName(relay.getName());
            relayRepository.save(relayByMac);
            response.setObject(relayByMac);
            response.setMsg("Relay added");
        } else {
            response.setObject(null);
            response.setMsg("Relay with such MAC does not exist");
        }
        return response;
    }

    @Override
    public Optional<Relay> deleteByIdForUser(Long relayId) {
        var user = getUser(userRepository);
        var relayOpt = relayRepository.findById(relayId);

        relayOpt.ifPresent(relay -> {
            if (relay.getUser().getId().equals(user.getId())){
                relay.setUser(null);
                relayRepository.save(relay);
            }
        });

        return relayOpt;
    }

    @Override
    public Optional<List<Relay>> getAll() {
        var user = getUser(userRepository);

        return relayRepository.findRelaysByUserIdOrderById(user.getId());
    }

    @Override
    public Optional<Relay> getById(Long id) {
        var user = getUser(userRepository);

        return relayRepository.getRelayByIdAndUserId(id, user.getId());
    }

    @Override
    public Optional<Relay> update(Long id, Relay relay) {
        var user = getUser(userRepository);

        var relayOpt = relayRepository.getRelayByIdAndUserId(id, user.getId());
        relayOpt.ifPresent(relay1 -> {
            relay1.setName(relay.getName());
            relay1.setOn(relay.getOn());
            relayRepository.save(relay1);
        });

        return relayRepository.findOne(Example.of(relay));
    }

    @Override
    public Optional<Relay> turnOnOf(Long relayId) {
        var user = getUser(userRepository);

        var relayOpt = relayRepository.getRelayByIdAndUserId(relayId, user.getId());
        return relayOpt.map(relay -> {
            relay.turn();
            relayRepository.save(relay);
            turnDevice(relay);
            return relay;
        });
    }
}
