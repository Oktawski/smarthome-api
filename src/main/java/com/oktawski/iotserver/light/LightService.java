package com.oktawski.iotserver.light;

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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class LightService {

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

    public ResponseEntity<List<Light>> getAll(String token){
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            List<Light> lights = userOpt.get().getLightList().stream()
                    .sorted(Comparator.comparing(Light::getId))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(lights, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Light> getById(String token, Long id){
        String username = jwtUtil.getUsername(token);

        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            List<Light> lights = userOpt.get().getLightList();
            return lights.stream()
                    .filter(v -> v.getId().equals(id))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        };

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Light> getByIp(String token, String ip){
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            List<Light> lights = userOpt.get().getLightList();
            return lights.stream()
                    .filter(v -> v.getIp().equals(ip))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    public ResponseEntity<BasicResponse<?>> add(String token, Light light){
        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        BasicResponse<?> basicResponse = new BasicResponse<>();

        userOpt.ifPresent(v -> {
            List<Light> lights = userOpt.get().getLightList();
            if(serviceHelper.isIpUnique(lights, light.getIp())){
                basicResponse.setMsg("Ip taken, choose another");
            }
            else{
                basicResponse.setMsg("Device added");
                light.setUser(v);
                lightRepo.save(light);
            }
        });

        if(lightRepo.exists(Example.of(light))){
            return new ResponseEntity<>(basicResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(basicResponse, HttpStatus.BAD_REQUEST);
    }

    /*TODO find out if getting device is faster from database and comparing it's user_id with user_id from user database
        or getting whole devices list from user and finding device by id
     */
    public ResponseEntity<Light> turnOnOf(String token, Long id) {

        AtomicReference<HttpStatus> httpStatus = new AtomicReference<>();

        String username = jwtUtil.getUsername(token);
        Optional<User> userOpt = userRepo.findUserByUsername(username);

        if(userOpt.isPresent()){
            Optional<Light> lightOpt = userOpt.get().getLightList().stream()
                    .filter(v -> v.getId().equals(id))
                    .findFirst();

            lightOpt.ifPresent(v -> {
                if(v.getOn()){
                    //TODO send data to ESP-8266 to turn of light
                }
                else{
                    //TODO send data to ESP-8266 to turn on light
                }

                v.turn();
                update(id, v);

                if(lightRepo.exists(Example.of(v))){
                    httpStatus.set(HttpStatus.OK);
                }
                else{
                    httpStatus.set(HttpStatus.BAD_REQUEST);
                }
            });
        }

        return new ResponseEntity<>(null, httpStatus.get());
    }

    public void setColor(Long id, short red, short green, short blue, short intensity){
        Optional<Light> optionalLight = lightRepo.findById(id);

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
        Optional<Light> optionalLight = lightRepo.findById(id);

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

    //TODO implement update
    public ResponseEntity<Light> update(String token, Long id, Light light) {
        Optional<Light> lightOptional =
                lightRepo.findById(id)
                        .map(v -> {
                            v.setIp(light.getIp());
                            v.setOn(light.getOn());
                            v.setIntensity(light.getIntensity());
                            v.setRed(light.getRed());
                            v.setBlue(light.getBlue());
                            v.setGreen(light.getGreen());
                            v.setName(light.getName());
                            return v;
                        });

        lightRepo.save(lightOptional.get());

        if(lightRepo.exists(Example.of(lightOptional.get()))){
            //todo send data to ESP-8266
            return new ResponseEntity<>(lightOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    //TODO implement update
    private void update(Long id, Light light){

    }

    //TODO create service superclass and implement some methods
    public ResponseEntity<Light> deleteById(Long id) {
        Optional<Light> lightOptional = lightRepo.findById(id);
        if(lightOptional.isPresent()){
            lightRepo.delete(lightOptional.get());
            if(!lightRepo.exists(Example.of(lightOptional.get()))){
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
