package com.oktawski.iotserver.superclasses;

import com.oktawski.iotserver.responses.BasicResponse;
import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.user.models.User;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

public interface IService <T extends WifiDevice>{
    BasicResponse<T> initDevice(String mac, String ip);
    BasicResponse<T> add(T t);
    Optional<T> deleteByIdForUser(Long id);
    Optional<List<T>> getAll();
    Optional<T> getById(Long id);
    Optional<T> update(Long id, T t);
    Optional<T> turnOnOf(Long id);

    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    default User getUser(UserRepository userRepository) {
        var user = userRepository.getUserByUsername(getUsername());
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("User with such username does not exist");
        }
    }

    @Async
    default void turnDevice(WifiDevice device) {
        try {
            URL url = new URL("http://" + device.getIp() + ":80/");
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url.toString());
            JSONObject json = new JSONObject();
            json.put("on", device.getOn());
            StringEntity stringEntity = new StringEntity(json.toString());
            request.setEntity(stringEntity);
            request.addHeader("content-type", "application/json");
            client.execute(request);
        } catch (UnknownHostException | MalformedURLException | UnsupportedEncodingException e) {
            System.out.printf("Unknown IP: %s%n", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
