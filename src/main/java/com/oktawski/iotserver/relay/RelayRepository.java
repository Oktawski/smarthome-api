package com.oktawski.iotserver.relay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RelayRepository")
interface RelayRepository extends JpaRepository<Relay, Long> {
}
