package com.oktawski.iotserver.user;

import com.oktawski.iotserver.responses.UserResponse;
import com.oktawski.iotserver.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){return service.all();}

    @PostMapping("signup")
    public ResponseEntity<UserResponse> signup(@RequestBody User user){
        return service.signup(user);
    }

    @PostMapping("signin")
    public ResponseEntity<UserResponse> signin(@RequestBody User user) {
        return service.signin(user);
    }

    @PostMapping("{user_id}/update")
    public ResponseEntity<?> update(@PathVariable("user_id") Long user_id, @RequestBody User user){
        return service.update(user_id, user);
    }
}
