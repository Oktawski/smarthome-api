package com.oktawski.iotserver.devices.relay;

import com.oktawski.iotserver.superclasses.WifiDevice;

import javax.persistence.*;

@Entity
@Table(name = "relays")
public class Relay extends WifiDevice {

    public Relay(){}

}
