package com.oktawski.iotserver.relay;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "relays")
public class Relay extends WifiDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "relay_id")
    Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Relay(){}

    public Relay(String name, String address, Boolean on){
        super(name, address, on);
    }

    public Relay(String name, String address, Boolean on, User user){
        super(name, address, on);
        this.user = user;
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
