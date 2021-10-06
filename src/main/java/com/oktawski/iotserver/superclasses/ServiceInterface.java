package com.oktawski.iotserver.superclasses;

import com.oktawski.iotserver.devices.relay.Relay;
import com.oktawski.iotserver.responses.BasicResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ServiceInterface <T extends WifiDevice>{
    BasicResponse<T> add(T t);
    Optional<T> deleteById(Long id);
    Optional<List<T>> getAll();
    Optional<T> getById(Long id);
    Optional<T> getByIp(String ip);
    Optional<T> update(Long id, T t);
    Optional<T> turnOnOff(Long id);

    default String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Async
    default void turn(WifiDevice relay) {
        try {
            System.out.println("Turn starts");  //temp

            String ip = relay.getIp();
            byte[] data = relay.getOn().toString().getBytes(StandardCharsets.UTF_8);
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
    }
}
