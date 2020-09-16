package com.oktawski.iotserver.light;

import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;
import org.hibernate.annotations.JoinColumnOrFormula;

import javax.persistence.*;

@Entity
@Table(name = "lights")
public class Light extends WifiDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "light_id")
    private Long id;

    private short red, green, blue, intensity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
