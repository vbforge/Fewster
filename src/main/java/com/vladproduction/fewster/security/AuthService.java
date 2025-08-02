package com.vladproduction.fewster.security;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Get the currently authenticated user
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found: " + username));
    }

    //Get the currently not-authenticated  demouser
    public User getDemoUser() {

        return userRepository.findByUsername("demouser")
                .orElseThrow(() -> new RuntimeException("Current user not found: demouser"));
    }

    //Get the current username
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    //Check if current user is the owner of the resource
    public boolean isCurrentUserOwner(Long resourceOwnerId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(resourceOwnerId);
    }
}
