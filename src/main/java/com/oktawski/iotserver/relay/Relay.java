package com.oktawski.iotserver.relay;

import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;

import javax.persistence.*;

@Entity
@Table(name = "relays")
public class Relay extends WifiDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "relay_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Relay(){}

    public Relay(String name, String address, Boolean on){
        super(name, address, on);
    }

    public Long getId(){
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
