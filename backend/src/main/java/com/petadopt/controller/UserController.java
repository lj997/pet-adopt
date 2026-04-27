package com.petadopt.controller;

import com.petadopt.common.Result;
import com.petadopt.dto.UserDTO;
import com.petadopt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/profile")
    public Result<UserDTO.UserResponse> updateProfile(@RequestBody UserDTO.UpdateRequest request) {
        UserDTO.UserResponse user = userService.updateUser(request);
        return Result.success("更新成功", user);
    }

    @GetMapping("/profile")
    public Result<UserDTO.UserResponse> getProfile() {
        UserDTO.UserResponse user = userService.getCurrentUserInfo();
        return Result.success(user);
    }
}
