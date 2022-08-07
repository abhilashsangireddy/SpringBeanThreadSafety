package com.experiment.SpringBeanThreadSafety.controllers;

import com.experiment.SpringBeanThreadSafety.entities.UserDto;
import com.experiment.SpringBeanThreadSafety.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public int addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }
}