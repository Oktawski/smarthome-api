package com.oktawski.iotserver.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.oktawski.iotserver.user.UserRepository;
import com.oktawski.iotserver.user.models.User;

@Service
public class CurrentUserProviderImpl implements CurrentUserProvider {

    private final UserRepository userRepository;

    @Autowired
    public CurrentUserProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        var userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        var user = userRepository.getUserById(userId);

        if (user == null) {
            throw new UsernameNotFoundException("User with such username does not exist");
        }

        return user;
    }
}
