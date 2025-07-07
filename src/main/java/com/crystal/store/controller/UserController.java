package com.crystal.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crystal.store.model.ResponseModel;
import com.crystal.store.model.UserModel;
import com.crystal.store.services.UserService;
import com.crystal.store.utils.ValidationUtils;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel> createUser(@Validated @RequestBody UserModel user) {
        // Additional email validation
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseModel(HttpStatus.BAD_REQUEST, "Invalid email format", null,
                            HttpStatus.BAD_REQUEST.value()));
        }

        UserModel createdUser = userService.createUser(user);
        return ResponseEntity
                .ok(new ResponseModel(HttpStatus.OK, "User created successfully", createdUser, HttpStatus.OK.value()));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseModel> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity
                .ok(new ResponseModel(HttpStatus.OK, "Users fetched successfully", users, HttpStatus.OK.value()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getUserById(@PathVariable Long id) {
        UserModel user = userService.getUserById(id);
        return ResponseEntity
                .ok(new ResponseModel(HttpStatus.OK, "User fetched successfully", user, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel> updateUser(@PathVariable Long id, @RequestBody UserModel user) {
        UserModel updatedUser = userService.updateUser(id, user);
        return ResponseEntity
                .ok(new ResponseModel(HttpStatus.OK, "User updated successfully", updatedUser, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity
                .ok(new ResponseModel(HttpStatus.OK, "User deleted successfully", null, HttpStatus.OK.value()));
    }
}
