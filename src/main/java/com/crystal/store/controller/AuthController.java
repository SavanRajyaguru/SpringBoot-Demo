package com.crystal.store.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crystal.store.dto.AuthRequest;
import com.crystal.store.dto.AuthResponse;
import com.crystal.store.dto.RegisterRequest;
import com.crystal.store.model.UserModel;
import com.crystal.store.services.JwtService;
import com.crystal.store.services.UserService;
import com.crystal.store.utils.Helper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
        String token = jwtService.generateToken(map);

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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            // Find user by email
            UserModel user = userService.findByEmail(loginRequest.getEmail());
            // Generate JWT token
            Map<String, String> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("userType", user.getUserType().toString());
            map.put("status", user.getStatus().toString());
            String token = jwtService.generateToken(map);

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
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder().message(e.getMessage()).build());
        }
    }
}
