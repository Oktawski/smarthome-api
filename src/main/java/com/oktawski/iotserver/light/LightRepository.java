package com.oktawski.iotserver.light;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("lightRepository")
public interface LightRepository extends JpaRepository<Light, Long> {

    @Query("SELECT light FROM Light light WHERE light.ip = ?1")
    Light findLightByIp(String ip);
}
