package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.UserDTO;
import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Value("${role.name}")
    private String role;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO signupUser(@RequestBody UserDTO userDTO){

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(role);

        userService.createUser(user);

        return userDTO;

    }

}
