package com.oktawski.iotserver.superclasses;

import com.oktawski.iotserver.responses.BasicResponse;

import java.util.List;
import java.util.Optional;

public interface IService <T extends WifiDevice>{
    BasicResponse<T> add(T t);
    Optional<T> deleteById(Long id);
    Optional<List<T>> getAll();
    Optional<T> getById(Long id);
    Optional<T> getByIp(String ip);
    Optional<T> update(Long id, T t);
    Optional<T> turnOnOf(Long id);
}
