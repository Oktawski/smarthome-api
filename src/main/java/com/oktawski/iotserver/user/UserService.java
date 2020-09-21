package com.oktawski.iotserver.user;

import com.oktawski.iotserver.user.models.LoginResponse;
import com.oktawski.iotserver.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(@Qualifier("userRepo") UserRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity signup(@Valid User user){
        if(repository.existsByEmail(user.getEmail())){
            return new ResponseEntity
                    ("Email taken", HttpStatus.BAD_REQUEST);
        }

        if(repository.existsByUsername(user.getUsername())){
            return new ResponseEntity
                    ("Username taken", HttpStatus.BAD_REQUEST);
        }

        //TODO implement password encryption to not violate constraints
        //user.setPassword(getEncoder().encode(user.getPassword()));
        repository.save(user);

        if(repository.exists(Example.of(user))){
            return new ResponseEntity
                    ("Account created", HttpStatus.OK);
        }

        return new ResponseEntity
                ("Something went wrong", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<LoginResponse> signin(User user){

        //TODO return some kind of token to verify client
        if(repository.existsByEmailAndPassword(user.getEmail(), user.getPassword())){
            User userLogged = repository.findByEmail(user.getEmail()).get();
            return new ResponseEntity<>
                    (new LoginResponse(userLogged, "Signed in"), HttpStatus.OK);
        }

        return new ResponseEntity<>
                (new LoginResponse(user, "Provided credentials do not match any user"), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> update(Long userId, User user){
        Optional<User> userToUpdate = repository.findById(userId);
        if(userToUpdate.get() == null){
            return new ResponseEntity<>("No such user", HttpStatus.BAD_REQUEST);
        }

        userToUpdate.map(v -> {
            v.setEmail(user.getEmail());
            v.setUsername(user.getUsername());
            v.setPassword(user.getUsername());
            return v;
        });
        repository.save(userToUpdate.get());
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }

    @Bean
    private PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    };
 }
