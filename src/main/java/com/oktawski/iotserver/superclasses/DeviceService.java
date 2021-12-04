package com.oktawski.iotserver.superclasses;

import org.springframework.data.jpa.repository.JpaRepository;

public class DeviceService <T extends WifiDevice> {

    private final JpaRepository<T, Long> repository;

    public DeviceService(JpaRepository repository) {
        this.repository = repository;
    }

    public T registerOrUpdateDevice(T device, String mac, String ip) {
        device.setMac(mac);
        device.setIp(ip);
        return repository.save(device);
    }
}
