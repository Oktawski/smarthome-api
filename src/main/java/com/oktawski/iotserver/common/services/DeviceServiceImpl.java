package com.oktawski.iotserver.common.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oktawski.iotserver.superclasses.WifiDevice;

public class DeviceServiceImpl<T extends WifiDevice> {

    private final JpaRepository<T, Long> repository;

    public DeviceServiceImpl(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public T registerOrUpdateDevice(T device, String mac, String ip) {
        device.setMac(mac);
        device.setIp(ip);
        return repository.save(device);
    }
}
