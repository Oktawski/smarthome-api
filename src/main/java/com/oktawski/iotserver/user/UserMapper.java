package com.oktawski.iotserver.user;

import com.oktawski.iotserver.user.models.User;
import com.oktawski.iotserver.user.models.UserDto;
import com.oktawski.iotserver.user.models.UserEditDto;
import com.oktawski.iotserver.user.requests.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public User toEntity(UserDto dto) {
        var user = new User();

        user.setUsername( dto.getUsername());
        user.setEmail(dto.getEmail());

        return user;
    }

    public User toEntity(UserEditDto dto) {
        var user = new User();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return user;
    }

    public User toEntity(RegisterRequest request) {
        var user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return user;
    }
}
