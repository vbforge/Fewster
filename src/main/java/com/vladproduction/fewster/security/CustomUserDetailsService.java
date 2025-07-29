package com.vladproduction.fewster.security;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${role.name}")
    private String role;

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
        System.out.println("=== DEBUG INFO ===");
        System.out.println("Username: " + username);
        System.out.println("Password starts with: " + user.getPassword().substring(0, Math.min(10, user.getPassword().length())));
        System.out.println("Role from DB: " + user.getRole());
        System.out.println("Role with prefix: " + roleWithPrefix);
        System.out.println("Authorities: " + authorities);
        System.out.println("Expected role from config: " + role);
        System.out.println("==================");

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );
    }

}
