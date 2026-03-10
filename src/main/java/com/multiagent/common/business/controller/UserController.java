package com.multiagent.common.business.controller;


import com.multiagent.common.business.service.UserService;
import com.multiagent.common.entity.UserLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/send")
    public void send(String phone) {
        userService.send(phone);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLogin userLogin) {
        return userService.login(userLogin);
    }
}
