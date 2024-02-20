//package com.oktawski.iotserver.common.interfaces;
//
//import com.oktawski.iotserver.superclasses.WifiDevice;
//
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.json.JSONObject;
//import org.springframework.scheduling.annotation.Async;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.UnknownHostException;
//import java.util.List;
//
//public interface DeviceService<T extends WifiDevice>{
//    Result<T> initDevice(String mac, String ip);
//    Result<T> add(T t);
//    Result<T> deleteByIdForUser(Long id);
//    Result<List<T>> getAll();
//    Result<T> getById(Long id);
//    Result<T> update(Long id, T t);
//    Result<T> turnOnOf(Long id);
//
//    @Async
//    default void turnDevice(WifiDevice device) {
//        try {
//            URL url = new URL("http://" + device.getIp() + ":80/");
//            CloseableHttpClient client = HttpClientBuilder.create().build();
//            HttpPost request = new HttpPost(url.toString());
//            JSONObject json = new JSONObject();
//            json.put("on", device.getOn());
//            StringEntity stringEntity = new StringEntity(json.toString());
//            request.setEntity(stringEntity);
//            request.addHeader("content-type", "application/json");
//            client.execute(request);
//        } catch (UnknownHostException | MalformedURLException | UnsupportedEncodingException e) {
//            System.out.printf("Unknown IP: %s%n", e.getMessage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
