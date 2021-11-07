package com.oktawski.iotserver.user.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oktawski.iotserver.light.Light;
import com.oktawski.iotserver.relay.Relay;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Getter @Setter @NoArgsConstructor
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
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Relay> relayList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Light> lightList;


    private boolean isNonExpired = true;

    private boolean isNonLocked = true;

    private boolean isEnabled = true;

    private boolean isCredentialsNonExpired = true;


    public User(
            @NotBlank @Size(max = 20) String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            List<? extends GrantedAuthority> grantedAuthorities)
    {
        this.username = username;
        this.email = email;
        this.password = password;
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
