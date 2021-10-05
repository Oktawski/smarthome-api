package com.oktawski.iotserver.devices.light;

import com.oktawski.iotserver.superclasses.WifiDevice;

import javax.persistence.*;

@Entity
@Table(name = "lights")
public class Light extends WifiDevice {

    private short red, green, blue, intensity;

    public Light(){}

    public Light(String name, String address){
        super(name, address);
    }

    public Light(String name, String address, short red, short green, short blue, short intensity, Boolean on) {
        super(name, address, on);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.intensity = intensity;
    }

    public short getRed() {
        return red;
    }

    public void setRed(short red) {
        this.red = red;
    }

    public short getGreen() {
        return green;
    }

    public void setGreen(short green) {
        this.green = green;
    }

    public short getBlue() {
        return blue;
    }

    public void setBlue(short blue) {
        this.blue = blue;
    }

    public short getIntensity() {
        return intensity;
    }

    public void setIntensity(short intensity) {
        this.intensity = intensity;
    }
}
