package com.petadopt.service;

import com.petadopt.dto.UserDTO;
import com.petadopt.entity.User;
import com.petadopt.repository.UserRepository;
import com.petadopt.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Transactional
    public UserDTO.UserResponse register(UserDTO.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    public Map<String, Object> login(UserDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToResponse(user));
        return result;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public UserDTO.UserResponse getCurrentUserInfo() {
        User user = getCurrentUser();
        return convertToResponse(user);
    }

    @Transactional
    public UserDTO.UserResponse updateUser(UserDTO.UpdateRequest request) {
        User user = getCurrentUser();

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getIntroduction() != null) {
            user.setIntroduction(request.getIntroduction());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    public UserDTO.UserResponse convertToResponse(User user) {
        UserDTO.UserResponse response = new UserDTO.UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setIntroduction(user.getIntroduction());
        response.setCity(user.getCity());
        return response;
    }
}
