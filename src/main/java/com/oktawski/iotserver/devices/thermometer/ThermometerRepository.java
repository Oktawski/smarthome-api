package com.oktawski.iotserver.devices.thermometer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("thermometerRepo")
interface ThermometerRepository extends JpaRepository<Thermometer, Long> {

    @Query("select thermometer from Thermometer thermometer where thermometer.ip = ?1")
    Thermometer findThermometerByIp(String ip);
    List<Thermometer> findByUserId(Long id);
}
