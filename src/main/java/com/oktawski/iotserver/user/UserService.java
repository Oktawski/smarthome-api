package com.oktawski.iotserver.user;

import com.oktawski.iotserver.responses.UserResponse;
import com.oktawski.iotserver.user.models.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Qualifier("userRepo") UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity all(){
        return new ResponseEntity(repository.findAll(), HttpStatus.OK);
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

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);

        if(repository.exists(Example.of(user))){
            return new ResponseEntity
                    ("Account created", HttpStatus.OK);
        }

        return new ResponseEntity
                ("Something went wrong", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ResponseEntity<?>> signin(User user){

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        if(repository.existsByEmailAndPassword(user.getEmail(), encodedPassword)){
            User loggedUser = repository.findByEmail(user.getEmail());
            return new ResponseEntity
                    ("OK", HttpStatus.OK);
        }

        return new ResponseEntity
                ("Provided credentials do not match any user", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> update(Long userId, User user){
        Optional<User> userToUpdate = repository.findById(userId);
        if(userToUpdate.isEmpty()){
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }
}
