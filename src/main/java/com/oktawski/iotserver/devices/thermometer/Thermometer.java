package com.oktawski.iotserver.devices.thermometer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;

import javax.persistence.*;

@Entity
@Table(name = "thermometers")
public class Thermometer extends WifiDevice {

    private double temperature;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user = null;

    public Thermometer(){}

    public double getTemperature() {
        return temperature;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
