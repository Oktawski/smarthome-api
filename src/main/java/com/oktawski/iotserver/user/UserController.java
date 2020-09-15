package com.oktawski.iotserver.user;

import com.oktawski.iotserver.user.models.LoginResponse;
import com.oktawski.iotserver.user.models.SignupResponse;
import com.oktawski.iotserver.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody User user){
        return service.signup(user);
    }

    @PostMapping("signin")
    public ResponseEntity<LoginResponse> signin(@RequestBody User user) {return service.signin(user);}
}
