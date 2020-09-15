package com.oktawski.iotserver.user.models;

import com.oktawski.iotserver.light.Light;
import com.oktawski.iotserver.relay.Relay;
import com.oktawski.iotserver.user.validators.ValidatePassword;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Email
    private String email;

    //TODO password validator
    @NotBlank
    @Size(max = 30)
    private String password;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_relays",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "relay_id"))
    private Set<Relay> relaySet = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_lights",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "light_id"))
    private Set<Light> lightSet = new HashSet<>();

    public User(){}

    public User(
            @NotBlank @Size(max = 20) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(max = 30) String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(
            Long id,
            @NotBlank @Size(max = 20) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(max = 30) String password)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Relay> getRelaySet() {
        return relaySet;
    }

    public void setRelaySet(Set<Relay> relaySet) {
        this.relaySet = relaySet;
    }

    public Set<Light> getLightSet() {
        return lightSet;
    }

    public void setLightSet(Set<Light> lightSet) {
        this.lightSet = lightSet;
    }
}
