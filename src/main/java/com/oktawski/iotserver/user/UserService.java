package com.oktawski.iotserver.user;

import com.oktawski.iotserver.user.models.LoginResponse;
import com.oktawski.iotserver.user.models.SignupResponse;
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

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(@Qualifier("userRepo") UserRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<SignupResponse> signup(User user){
        if(repository.existsByEmail(user.getEmail())){
            return new ResponseEntity<>
                    (new SignupResponse(user, "Email taken"), HttpStatus.BAD_REQUEST);
        }

        if(repository.existsByUsername(user.getUsername())){
            return new ResponseEntity<>
                    (new SignupResponse(user, "Username taken"), HttpStatus.BAD_REQUEST);
        }

        //TODO implement password encryption to not violate constraints
        //user.setPassword(getEncoder().encode(user.getPassword()));
        repository.save(user);

        if(repository.exists(Example.of(user))){
            return new ResponseEntity<>
                    (new SignupResponse(user, "Account created"), HttpStatus.OK);
        }

        return new ResponseEntity<>
                (new SignupResponse(null, "Something went wrong"), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<LoginResponse> signin(User user){

        if(repository.existsByEmailAndPassword(user.getEmail(), user.getPassword())){
            User userLogged = repository.findByEmail(user.getEmail()).get();
            return new ResponseEntity<>
                    (new LoginResponse(userLogged, "Signed in"), HttpStatus.OK);
        }

        return new ResponseEntity<>
                (new LoginResponse(user, "Provided credentials do not match any user"), HttpStatus.BAD_REQUEST);
    }

    @Bean
    private PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    };
 }
