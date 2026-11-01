
package com.arun.ecommerce.service;

import com.arun.ecommerce.dto.UserRegisterRequest;
import com.arun.ecommerce.entity.Role;
import com.arun.ecommerce.entity.User;
import com.arun.ecommerce.exception.EmailAlreadyExistsException;
import com.arun.ecommerce.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(UserRegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                "Email is already registered"
            );
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }
}
