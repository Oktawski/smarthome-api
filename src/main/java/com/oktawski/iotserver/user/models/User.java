package com.oktawski.iotserver.user.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oktawski.iotserver.devices.light.Light;
import com.oktawski.iotserver.devices.relay.Relay;
import com.oktawski.iotserver.devices.thermometer.Thermometer;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
@Table(name = "users")
public class User implements UserDetails {

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
    @Column(name="password", length=9999)
    private String password;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Relay> relayList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Light> lightList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Thermometer> thermometerList;


    private boolean isNonExpired = true;

    private boolean isNonLocked = true;

    private boolean isEnabled = true;

    private boolean isCredentialsNonExpired = true;


    public User(){}

    public User(
            @NotBlank @Size(max = 20) String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            List<? extends GrantedAuthority> grantedAuthorities)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = true;
    }

    public User(
            Long id,
            @NotBlank @Size(max = 20) String username,
            @NotBlank @Email String email,
            @NotBlank String password)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = true;
    }

    public Relay getRelayById(Long id){
        return this.relayList.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Relay getRelayByIp(String ip){
        return this.relayList.stream()
                .filter(v -> v.getIp().equals(ip))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Light getLightById(Long id){
        return this.lightList.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Light getLightByIp(String ip){
        return this.lightList.stream()
                .filter(v -> v.getIp().equals(ip))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Thermometer getThermometerById(Long id){
        return this.thermometerList.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Thermometer getThermometerByIp(String ip){
        return this.thermometerList.stream()
                .filter(v -> v.getIp().equals(ip))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
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

    public List<Relay> getRelayList() {
        return relayList;
    }

    public List<Thermometer> getThermometerList() {
        return thermometerList;
    }

    public List<Light> getLightList(){
        return lightList;
    }

    public void setRelayList(List<Relay> relayList) {
        this.relayList = relayList;
    }

    public void setLightList(List<Light> lightList) {
        this.lightList = lightList;
    }

    public void setThermometerList(List<Thermometer> thermometerList) {
        this.thermometerList = thermometerList;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
