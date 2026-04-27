package com.petadopt.dto;

import com.petadopt.enums.UserRole;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserDTO {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        @NotBlank(message = "昵称不能为空")
        private String nickname;

        @NotNull(message = "角色不能为空")
        private UserRole role;

        private String phone;

        private String email;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String username;
        private String nickname;
        private UserRole role;
        private String phone;
        private String email;
        private String avatar;
        private String introduction;
        private String city;
    }

    @Data
    public static class UpdateRequest {
        private String nickname;
        private String phone;
        private String email;
        private String avatar;
        private String introduction;
        private String city;
    }
}
