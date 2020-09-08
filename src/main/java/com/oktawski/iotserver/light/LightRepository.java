package com.oktawski.iotserver.light;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("lightRepository")
public interface LightRepository extends JpaRepository<Light, Long> {

}
