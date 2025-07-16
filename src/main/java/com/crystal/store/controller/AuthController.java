package com.crystal.store.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crystal.store.config.JWTConfig;
import com.crystal.store.dto.AuthRequest;
import com.crystal.store.dto.AuthResponse;
import com.crystal.store.dto.RegisterRequest;
import com.crystal.store.model.UserModel;
import com.crystal.store.services.UserService;
import com.crystal.store.utils.Helper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Create a new user from the request
        UserModel user = new UserModel();
        user.setName(registerRequest.getName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(Helper.passwordEncoder().encode(registerRequest.getPassword()));

        // Save the user
        UserModel savedUser = userService.createUser(user);

        // Generate JWT token
        Map<String, String> map = new HashMap<>();
        map.put("username", savedUser.getUsername());
        map.put("userType", savedUser.getUserType().toString());
        map.put("status", savedUser.getStatus().toString());
        String token = jwtConfig.generateToken(map);

        // Create response
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .userType(savedUser.getUserType())
                .message("User registered successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest loginRequest) {

        // Find user by email
        System.out.println("Email: " + loginRequest.getEmail());
        UserModel user = userService.findByEmail(loginRequest.getEmail());
        System.out.println("User: " + user);

        // Check if user exists and password is correct
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Invalid username or password").build());
        }

        if (!Helper.passwordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Invalid username or password").build());
        }

        // Generate JWT token
        Map<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("userType", user.getUserType().toString());
        map.put("status", user.getStatus().toString());
        String token = jwtConfig.generateToken(map);

        // Create response
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userType(user.getUserType())
                .message("Login successful")
                .build();

        return ResponseEntity.ok(response);
    }
}
