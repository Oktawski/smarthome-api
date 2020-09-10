package com.oktawski.iotserver.relay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("relayRepo")
interface RelayRepository extends JpaRepository<Relay, Long> {

    @Query("select relay from Relay relay where relay.ip = ?1")
    Relay findRelayByIp(String ip);
}
