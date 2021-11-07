package com.oktawski.iotserver.relay;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "relays")
public class Relay extends WifiDevice {

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user = null;
}
