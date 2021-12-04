package com.oktawski.iotserver.relay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("relayRepository")
interface RelayRepository extends JpaRepository<Relay, Long> {

    @Query("select relay from Relay relay where relay.mac = ?1")
    Relay findRelayByMac(String mac);
    Optional<Relay> getRelayByIdAndUserId(Long relayId, Long userId);
    Optional<List<Relay>> findRelaysByUserIdOrderById(Long userId);
    Relay findRelayByIdAndUserId(Long id, Long userId);
}
