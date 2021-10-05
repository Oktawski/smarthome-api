package com.oktawski.iotserver.devices.thermometer;

import com.oktawski.iotserver.superclasses.WifiDevice;

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
