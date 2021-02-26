package com.oktawski.iotserver.superclasses;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class WifiDevice {

    String name;

    @Column(name = "ip")
    @NotNull
    String ip;

    @Column(name = "is_on")
    Boolean on = false;

    public WifiDevice(){}

    public WifiDevice(String name, String ip){
        this.name = name;
        this.ip = ip;
    }

    public WifiDevice(String name, String ip, Boolean on){
        this.name = name;
        this.ip = ip;
        this.on = on;
    }

    public void turn(){
        on = getOn() ? false : true;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
