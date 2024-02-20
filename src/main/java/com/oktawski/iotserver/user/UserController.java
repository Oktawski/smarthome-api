package com.oktawski.iotserver.user;

import com.oktawski.iotserver.common.SimpleResult;
import com.oktawski.iotserver.common.Status;
import com.oktawski.iotserver.user.models.User;
import com.oktawski.iotserver.user.models.UserDto;
import com.oktawski.iotserver.user.models.UserEditDto;
import com.oktawski.iotserver.user.requests.LoginRequest;
import com.oktawski.iotserver.user.requests.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        var result = service.register(request);

        return GetResponseEntity(result);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var result = service.login(request);

        return GetResponseEntity(result);
    }

    @PostMapping("{user_id}/update")
    public ResponseEntity<?> update(@PathVariable("user_id") Long userId, @RequestBody UserEditDto userEditDto) {
        var result = service.update(userId, userEditDto);

        return GetResponseEntity(result);
    }

    private ResponseEntity<?> GetResponseEntity(SimpleResult result) {
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
    }
}
