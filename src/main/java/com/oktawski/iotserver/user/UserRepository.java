package com.oktawski.iotserver.user;

import com.oktawski.iotserver.user.models.User;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username) throws ObjectNotFoundException;
    User getUserById(Long userId);
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndPassword(String email, String password);
}
