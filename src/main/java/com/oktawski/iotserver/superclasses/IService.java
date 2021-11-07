package com.oktawski.iotserver.superclasses;

import com.oktawski.iotserver.relay.Relay;
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

public interface IService <T extends WifiDevice>{
    BasicResponse<T> add(T t);
    Optional<T> deleteById(Long id);
    Optional<List<T>> getAll();
    Optional<T> getById(Long id);
    Optional<T> update(Long id, T t);
    Optional<T> turnOnOf(Long id);

    default String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
