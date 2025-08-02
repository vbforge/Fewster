package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.repository.UserRepository;
import com.vladproduction.fewster.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(User user) {
        log.info("Attempting to create and save user: {}", user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Password: {} for user: {} has been successfully encoded.", user.getPassword(), user);

        userRepository.save(user);
        log.info("User: {} saved successfully.", user);
    }
}
