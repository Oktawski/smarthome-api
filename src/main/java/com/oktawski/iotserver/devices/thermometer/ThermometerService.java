package com.oktawski.iotserver.devices.thermometer;

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
public class ThermometerService implements ServiceInterface<Thermometer> {

    private final ThermometerRepository thermometerRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    private final ServiceHelper serviceHelper;

    @Autowired
    public ThermometerService(@Qualifier("thermometerRepo") ThermometerRepository thermometerRepo,
                        @Qualifier("userRepo") UserRepository userRepo,
                        JwtUtil jwtUtil,
                        ServiceHelper serviceHelper){
        this.thermometerRepo = thermometerRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.serviceHelper = serviceHelper;
    }


    @Override
    public BasicResponse<Thermometer> add(Thermometer thermometer) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        var response = new BasicResponse<Thermometer>();

        userOpt.ifPresent(v -> {
            var thermometers = userOpt.get().getThermometerList();
            if(!serviceHelper.isIpUnique(thermometers, thermometer.getIp())){
                thermometer.setUser(v);
                thermometerRepo.save(thermometer);

                response.setObject(thermometer);
                response.setMsg("Thermometer added");

                //turn(thermometer);
            }
            else{
                response.setObject(null);
                response.setMsg(String.format("Thermometer with ip: %s already exists", thermometer.getIp()));
            }
        });
        return response;
    }

    @Override
    public Optional<Thermometer> deleteById(Long thermometerId) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);
        var thermometerOpt = thermometerRepo.findById(thermometerId);

        thermometerOpt.ifPresent(thermometer -> {
            userOpt.ifPresent(user -> {
                if(thermometer.getUser().getId().equals(user.getId())){
                    thermometerRepo.delete(thermometer);
                }
            });
        });

        if(thermometerRepo.findById(thermometerId).isEmpty()){
            return Optional.empty();
        }
        return thermometerOpt;
    }

    @Override
    public Optional<List<Thermometer>> getAll() {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(v -> {
            List<Thermometer> thermometers = v.getThermometerList().stream()
                    .sorted(Comparator.comparing(Thermometer::getId))
                    .collect(Collectors.toList());
            return Optional.of(thermometers);
        }).orElse(Optional.empty());
    }

    @Override
    public Optional<Thermometer> getById(Long id) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            return userOpt.map(v -> Optional.of(v.getThermometerById(id)))
                    .orElse(null);
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Thermometer>getByIp(String ip) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(v -> Optional.of(v.getThermometerByIp(ip)))
                .orElse(null);
    }



    @Override
    public Optional<Thermometer> update(Long id, Thermometer thermometer) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            var thermometerOpt = userOpt.map(v -> v.getThermometerById(id));
            thermometerOpt.map(v -> {
                v.setName(thermometer.getName());
                v.setIp(thermometer.getIp());
                v.setOn(thermometer.getOn());
                return v;
            });

            thermometerOpt.ifPresent(thermometerRepo::save);

            if(thermometerRepo.existsById(id)){
                return thermometerOpt;
            }
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Thermometer> turnOnOf(Long thermometerId) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try {
            var thermometerOpt = userOpt.map(v -> v.getThermometerById(thermometerId));
            thermometerOpt.map(v -> {
                v.turn();
                thermometerRepo.save(v);
                return v;
            });

            if (thermometerRepo.existsById(thermometerId)) {
                turn(thermometerOpt.get());
                return thermometerOpt;
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    // TODO test with ESP-01 module
/*    @Async
    protected void turn(Thermometer thermometer) {
        try {
            System.out.println("Turn starts");  //temp

            String ip = thermometer.getIp();
            byte[] data = thermometer.getOn().toString().getBytes(StandardCharsets.UTF_8);
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
