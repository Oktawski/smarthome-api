package com.oktawski.iotserver.user;

import com.oktawski.iotserver.common.SimpleResult;
import com.oktawski.iotserver.user.models.User;
import com.oktawski.iotserver.user.models.UserEditDto;
import com.oktawski.iotserver.user.requests.LoginRequest;
import com.oktawski.iotserver.user.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public ResponseEntity<List<User>> all() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    public SimpleResult register(@Valid RegisterRequest request) {
        if(repository.existsByEmail(request.getEmail())){
            return SimpleResult.warning("Email taken");
        }

        if(repository.existsByUsername(request.getUsername())) {
            return SimpleResult.warning("Username taken");
        }

        var user = userMapper.toEntity(request);

        repository.save(user);

        if(repository.exists(Example.of(user))) {
            return SimpleResult.success("Account created");
        }

        return SimpleResult.error("Something went wrong");
    }

    public SimpleResult login(LoginRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        var user = repository.findByEmailAndPassword(request.getEmail(), encodedPassword);

        if(user == null){
            return SimpleResult.error("Wrong credentials");
        }

        return SimpleResult.success("Welcome " + user.getUsername());
    }

    public SimpleResult update(Long userId, UserEditDto editDto) {
        Optional<User> userToUpdate = repository.findById(userId);

        if(userToUpdate.isEmpty()) {
            return SimpleResult.error("No such user");
        }

        userToUpdate.map(e -> userMapper.toEntity(editDto));

        repository.save(userToUpdate.get());

        return SimpleResult.success("User updated");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username %s not found", username));
        }
        return user;
    }
}
