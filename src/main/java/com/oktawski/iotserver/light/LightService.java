package com.oktawski.iotserver.light;

import com.oktawski.iotserver.superclasses.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LightService implements IService<Light> {

    private final LightRepository repository;

    @Autowired
    public LightService(@Qualifier("lightRepository") LightRepository repository){
        this.repository = repository;
    }

    //TODO allow only unique ip to be added
    @Override
    public ResponseEntity<Light> add(Light light){
        repository.save(light);

        if(repository.exists(Example.of(light))){
            return new ResponseEntity<>(light, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    //TODO create service superclass and implement some methods
    @Override
    public ResponseEntity<Light> deleteById(Long id) {
        Optional<Light> lightOptional = repository.findById(id);
        if(lightOptional.isPresent()){
            repository.delete(lightOptional.get());
            if(!repository.exists(Example.of(lightOptional.get()))){
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Light>> getAll(){
        List<Light> lights = repository.findAll();
        if(!lights.isEmpty()){
            return new ResponseEntity<>(lights, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Light> getById(Long id){
        Optional<Light> lightOptional = repository.findById(id);
        if(lightOptional.isPresent()){
            return new ResponseEntity<>(lightOptional.get(), HttpStatus.FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Light> getByIp(String ip){
        Light light = repository.findLightByIp(ip);
        if(light != null){
            return new ResponseEntity<>(light, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Light> update(Long id, Light light) {
        Optional<Light> lightOptional =
                repository.findById(id)
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

        repository.save(lightOptional.get());

        if(repository.exists(Example.of(lightOptional.get()))){
            //todo send data to ESP-8266
            return new ResponseEntity<>(lightOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Light> turnOnOf(Long id) {
        Optional<Light> lightOpt = repository.findById(id);
        if(lightOpt.isPresent()) {
            Light light = lightOpt.get();
            if (light.getOn()) {
                //todo send data to ESP-8266 to turn of light
                //ArduinoController turnOf(
            } else {
                //todo send data to ESP-8266 to turn on light
            }
            light.turn();
            update(id, light);

            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    public void setColor(Long id, short red, short green, short blue, short intensity){
        Optional<Light> optionalLight = repository.findById(id);

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
        Optional<Light> optionalLight = repository.findById(id);

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
