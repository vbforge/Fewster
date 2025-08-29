package com.vladproduction.fewster.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping
public class TestController {

    @GetMapping("/test")
    public String get(Principal principal, HttpServletRequest request){
        return principal + " ;" + request.getRemoteUser();
    }

}
