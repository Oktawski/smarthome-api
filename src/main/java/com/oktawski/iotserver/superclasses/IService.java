package com.oktawski.iotserver.superclasses;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IService <T extends Object>{
    ResponseEntity<T> add(T t);
    ResponseEntity<T> deleteById(Long id);
    ResponseEntity<List<T>> getAll();
    ResponseEntity<T> getById(Long id);
    ResponseEntity<T> update(Long id, T t);
    ResponseEntity<T> turnOnOf(Long id);
}
