package com.oktawski.iotserver.light;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("lightRepository")
public interface LightRepository extends JpaRepository<Light, Long> {

    @Query("select light from Light light where light.mac = ?1")
    Light findLightByMac(String mac);
    Optional<List<Light>> findLightByUserIdOrderById(Long userId);
    Optional<Light> findLightByIdAndUserId(Long lightId, Long userId);
}
