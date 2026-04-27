package com.petadopt.controller;

import com.petadopt.common.Result;
import com.petadopt.dto.UserDTO;
import com.petadopt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<UserDTO.UserResponse> register(@Valid @RequestBody UserDTO.RegisterRequest request) {
        UserDTO.UserResponse user = userService.register(request);
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserDTO.LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        return Result.success("登录成功", result);
    }

    @GetMapping("/me")
    public Result<UserDTO.UserResponse> getCurrentUser() {
        UserDTO.UserResponse user = userService.getCurrentUserInfo();
        return Result.success(user);
    }
}
