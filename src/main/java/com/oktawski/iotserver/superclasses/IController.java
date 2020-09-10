package com.oktawski.iotserver.superclasses;

import org.springframework.http.ResponseEntity;

import java.util.List;


//now it is the same as IService but in the future cann be different
public interface IController  <T extends WifiDevice>{
    ResponseEntity<T> add(T t);
    ResponseEntity<T> deleteById(Long id);
    ResponseEntity<List<T>> getAll();
    ResponseEntity<T> getById(Long id);
    ResponseEntity<T> getByIp(String ip);
    ResponseEntity<T> update(Long id, T t);
    ResponseEntity<T> turnOnOf(Long id);
}