package com.oktawski.iotserver.superclasses;

import com.oktawski.iotserver.responses.BasicResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IService <T extends WifiDevice>{
    ResponseEntity<BasicResponse<T>> add(String token, T t);
    ResponseEntity<?> deleteById(String token, Long id);
    ResponseEntity<List<T>> getAll(String token);
    ResponseEntity<T> getById(String token, Long id);
    ResponseEntity<T> getByIp(String token, String ip);
    ResponseEntity<T> update(String token, Long id, T t);
    ResponseEntity<T> turnOnOf(String token, Long id);
}
