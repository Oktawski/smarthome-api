package com.oktawski.iotserver.light;

import com.oktawski.iotserver.jwt.JwtUtil;
import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.superclasses.DeviceService;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LightService extends DeviceService<Light> implements IService<Light> {

    private final LightRepository lightRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ServiceHelper serviceHelper;

    @Autowired
    public LightService(@Qualifier("lightRepository") LightRepository lightRepo,
                        @Qualifier("userRepository") UserRepository userRepo,
                        JwtUtil jwtUtil,
                        ServiceHelper serviceHelper){
        super(lightRepo);
        this.lightRepository = lightRepo;
        this.userRepository = userRepo;
        this.jwtUtil = jwtUtil;
        this.serviceHelper = serviceHelper;
    }

    @Override
    public BasicResponse<Light> initDevice(String mac, String ip) {
        var light = lightRepository.findLightByMac(mac);
        light = registerOrUpdateDevice(Objects.requireNonNullElseGet(light, Light::new), mac, ip);

        System.out.println(light.getMac());
        System.out.println(light.getIp());

        return new BasicResponse<>(light, "Light registered");
    }

    @Override
    public BasicResponse<Light> add(Light light) {
        var user = getUser(userRepository);

        var response = new BasicResponse<Light>();

        var lightByMac = lightRepository.findLightByMac(light.getMac());
        if (lightByMac != null) {
            lightByMac.setUser(user);
            lightByMac.setName(light.getName());
            lightRepository.save(lightByMac);
            response.setObject(lightByMac);
            response.setMsg("Light added");
        } else {
            response.setObject(null);
            response.setMsg("Light with such MAC does not exist");
        }
        return response;
    }

    @Override
    public Optional<Light> deleteByIdForUser(Long id) {
        var user = getUser(userRepository);
        var lightOpt = lightRepository.findById(id);

        lightOpt.ifPresent(light -> {
            if (light.getUser().getId().equals(user.getId())) {
                light.setUser(null);
                lightRepository.save(light);
            }
        });

        return lightOpt;
    }

    @Override
    public Optional<List<Light>> getAll() {
        var user = getUser(userRepository);

        return Optional.of(lightRepository.getLightsByUserIdOrderById(user.getId()));
    }

    @Override
    public Optional<Light> getById(Long id) {
        var user = getUser(userRepository);

        return lightRepository.findLightByIdAndUserId(id, user.getId());
    }

    @Override
    public Optional<Light> update(Long id, Light light) {
        var user = getUser(userRepository);

        var lightOpt = lightRepository.findLightByIdAndUserId(id, user.getId());
        lightOpt.ifPresent(mLight -> {
            mLight.setName(light.getName());
            mLight.setOn(light.getOn());
            lightRepository.save(mLight);
        });

        return lightRepository.findById(id);
    }

    @Override
    public Optional<Light> turnOnOf(Long id) {
        var user = getUser(userRepository);

        var lightOpt = lightRepository.findLightByIdAndUserId(id, user.getId());
        return lightOpt.map(light -> {
            light.turn();
            lightRepository.save(light);
            //turnDevice(light);
            return light;
        });
    }

    public void setColorAndIntensity(Long id, int red, int green, int blue, int intensity){
        var optionalLight = lightRepository.findById(id);

        optionalLight.ifPresent(light -> {
            light.setColorsAndIntensity(red, green, blue, intensity);
            lightRepository.save(light);
            changeLightsRequest(light);
        });
    }

    @Async
    protected void changeLightsRequest(Light light) {
        try {
            URL url = new URL("http://" + light.getIp() + ":80");
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url.toString());
            JSONObject json = new JSONObject();
            json.put("red", light.getRed());
            json.put("green", light.getGreen());
            json.put("blue", light.getBlue());
            json.put("intensity", light.getIntensity());
            StringEntity stringEntity = new StringEntity(json.toString());
            request.setEntity(stringEntity);
            request.addHeader("content-type", "application/json");
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
