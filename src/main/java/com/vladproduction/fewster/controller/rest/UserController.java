package com.vladproduction.fewster.controller.rest;

import com.vladproduction.fewster.dto.UserDTO;
import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Value("${role.name}")
    private String role;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //http://localhost:8080/api/v1/user
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody UserDTO userDTO){

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(role);

        userService.createUser(user);

        return userDTO;

    }

}
