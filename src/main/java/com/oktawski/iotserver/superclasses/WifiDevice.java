package com.oktawski.iotserver.superclasses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
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
        on = !on;
    }
}
