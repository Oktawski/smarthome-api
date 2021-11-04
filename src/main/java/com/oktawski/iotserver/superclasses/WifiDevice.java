package com.oktawski.iotserver.superclasses;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class WifiDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String ip;

    @NotNull
    private String mac;

    @Column(name = "is_on")
    private Boolean on = false;

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
        on = !getOn();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Boolean getOn() {
        return on;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }
}
