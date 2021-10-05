package com.oktawski.iotserver.devices.thermometer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;

import javax.persistence.*;

@Entity
@Table(name = "thermometers")
public class Thermometer extends WifiDevice {

    private double temperature;

    public Thermometer(){}

    public double getTemperature() {
        return temperature;
    }
}
