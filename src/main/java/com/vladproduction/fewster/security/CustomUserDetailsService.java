package com.vladproduction.fewster.security;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Value("${role.name}")
    private String role;

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User with " + username + " is not found"));

        // ROLE_ prefix to match of how Spring handle ROLES
        String roleWithPrefix = user.getRole().startsWith("ROLE_") ?
                user.getRole() : "ROLE_" + user.getRole();

        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(roleWithPrefix));

        //debug log
        log.debug("=== DEBUG INFO ===");
        log.debug("Username: {}", username);
        log.debug("Password starts with: {}", user.getPassword().substring(0, Math.min(10, user.getPassword().length())));
        log.debug("Role from DB: {}", user.getRole());
        log.debug("Role with prefix: {}", roleWithPrefix);
        log.debug("Authorities: {}", authorities);
        log.debug("Expected role from config: {}", role);
        log.debug("==================");

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );
    }

}
