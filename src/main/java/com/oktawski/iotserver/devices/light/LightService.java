package com.oktawski.iotserver.devices.light;

import com.oktawski.iotserver.jwt.JwtUtil;
import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.user.models.User;
import com.oktawski.iotserver.utilities.ServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import com.oktawski.iotserver.superclasses.ServiceInterface;

@Service
public class LightService implements ServiceInterface<Light> {

    private final LightRepository lightRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final ServiceHelper serviceHelper;

    @Autowired
    public LightService(@Qualifier("lightRepository") LightRepository lightRepo,
                        @Qualifier("userRepository") UserRepository userRepo,
                        JwtUtil jwtUtil,
                        ServiceHelper serviceHelper){
        this.lightRepo = lightRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.serviceHelper = serviceHelper;
    }


    /*TODO find out if getting device is faster from database and comparing it's user_id with user_id from user database
        or getting whole devices list from user and finding device by id in turnOnOff method
     */

    @Override
    public BasicResponse<Light> add(Light light) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        var response = new BasicResponse<Light>();

        userOpt.ifPresent(v -> {
            List<Light> lights = v.getLightList();
            if(!serviceHelper.isIpUnique(lights, light.getIp())){
                light.setUser(v);
                lightRepo.save(light);

                response.setObject(light);
                response.setMsg("Light added");
            }

            else{
                response.setObject(null);
                response.setMsg(String.format("Light with ip: %s already exists", light.getIp()));
            }
        });

        return response;
    }

    @Override
    public Optional<Light> deleteById(Long id) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);
        var lightOpt = lightRepo.findById(id);

        lightOpt.ifPresent(light -> {
            userOpt.ifPresent(user -> {
                if(light.getUser().equals(user)){
                    lightRepo.delete(light);
                }
            });
        });

        if(lightRepo.findById(id).isEmpty()){
            return Optional.empty();
        }
        return lightOpt;
    }

    @Override
    public Optional<List<Light>> getAll() {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        return userOpt.map(v -> {
            var lights = v.getLightList().stream()
                    .sorted(Comparator.comparing(Light::getId))
                    .collect(Collectors.toList());

            return Optional.of(lights);
        }).orElse(Optional.empty());
    }

    @Override
    public Optional<Light> getById(Long id) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try{
            return userOpt.map(v -> Optional.of(v.getLightById(id)))
                    .orElse(null);
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Light> getByIp(String ip){
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try{
            return userOpt.map(v -> Optional.of(v.getLightByIp(ip)))
                    .orElse(null);
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Light> update(Long id, Light light) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try{
            var lightOpt = userOpt.map(v -> v.getLightById(id));
            lightOpt.map(v -> {
                v.setName(light.getName());
                v.setIp(light.getIp());
                v.setOn(light.getOn());
                v.setRed(light.getRed());
                v.setGreen(light.getGreen());
                v.setBlue(light.getBlue());
                v.setIntensity(light.getIntensity());
                return v;
            });

            lightOpt.ifPresent(lightRepo::save);

            if(lightRepo.existsById(id)){
                return lightOpt;
            }
        }
        catch(NoSuchElementException e){
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Light> turnOnOff(Long id) {
        var username = getUsername();
        var userOpt = userRepo.findUserByUsername(username);

        try{
            var lightOpt = userOpt.map(v -> v.getLightById(id));
            lightOpt.map(v -> {
                v.turnOnOff();
                lightRepo.save(v);
                return v;
            });

            if(lightRepo.existsById(id)){
                turn(lightOpt.get());
                return lightOpt;
            }
            return Optional.empty();
        }catch(NoSuchElementException e){
            return Optional.empty();
        }
    }

    public void setColor(Long id, short red, short green, short blue, short intensity){
        var optionalLight = lightRepo.findById(id);

        if(optionalLight.isPresent()){
            optionalLight.map(
                    light -> {
                        light.setRed(red);
                        light.setGreen(green);
                        light.setBlue(blue);
                        light.setIntensity(intensity);
                        return light;
                    });

            update(id, optionalLight.get());
        }

        //TODO send data to ESP8266
    }

    public void setColor(Long id, short intensity, short[] rgb){
        var optionalLight = lightRepo.findById(id);

        if(optionalLight.isPresent()){
            optionalLight.map(
                    light -> {
                        light.setRed(rgb[0]);
                        light.setGreen(rgb[1]);
                        light.setBlue(rgb[2]);
                        light.setIntensity(intensity);
                        return light;
                    });

            update(id, optionalLight.get());
        }

        //TODO send data to ESP8266
    }
}
