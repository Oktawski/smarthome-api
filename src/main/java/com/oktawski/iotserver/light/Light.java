package com.oktawski.iotserver.light;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.oktawski.iotserver.superclasses.WifiDevice;
import com.oktawski.iotserver.user.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "lights")
public class Light extends WifiDevice {

    private int red, green, blue, intensity = 0;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user = null;

   /*
    public Light(String name, String address){
        super(name, address);
    }

    public Light(String name, String mac, Boolean on) {
        super(name, mac, on);
    }

    public Light(String name, String address, int red, int green, int blue, int intensity, Boolean on) {
        super(name, address, on);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.intensity = intensity;
    }
*/
    public void setColorsAndIntensity(int r, int g, int b, int intensity) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.intensity = intensity;
    }
}
