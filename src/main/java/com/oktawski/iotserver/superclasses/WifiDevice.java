package com.oktawski.iotserver.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class WifiDevice {

    String name, address;

    @Column(name = "is_on")
    Boolean on = false;

    public WifiDevice(){}

    public WifiDevice(String name, String address){
        this.name = name;
        this.address = address;
    }

    public WifiDevice(String name, String address, Boolean on){
        this.name = name;
        this.address = address;
        this.on = on;
    }

    public void turn(){
        on = getOn() ? false : true;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOn() {
        return on;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }
}
