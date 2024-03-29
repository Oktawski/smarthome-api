package com.oktawski.iotserver.superclasses;

import com.oktawski.iotserver.responses.BasicResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface IController  <T extends WifiDevice>{
    ResponseEntity<BasicResponse<T>> add(T t);
    ResponseEntity<?> deleteById(Long id);
    ResponseEntity<List<T>> getAll();
    ResponseEntity<T> getById(Long id);
    ResponseEntity<T> update(Long id, T t);
    ResponseEntity<T> turnOnOf(Long id);
}