package com.oktawski.iotserver.light;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("lightRepository")
public interface LightRepository extends JpaRepository<Light, Long> {

    @Query("select light from Light light where light.ip = ?1")
    Light findLightByIp(String ip);
}
