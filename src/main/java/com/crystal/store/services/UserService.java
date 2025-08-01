package com.crystal.store.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.crystal.store.exception.ResourceNotFoundException;
import com.crystal.store.enums.Enums.Status;
import com.crystal.store.enums.Enums.UserType;
import com.crystal.store.exception.InternalServerErrorException;
import com.crystal.store.model.UserModel;
import com.crystal.store.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user
     */
    public UserModel createUser(UserModel user) {
        if (userRepository.existsByEmail(user.getEmail())) {

            throw new DataIntegrityViolationException("User with this email already exists");
        }
        if (user.getUserType() == null) {
            user.setUserType(UserType.USER);
        }
        if (user.getStatus() == null) {
            user.setStatus(Status.ACTIVE);
        }
        return userRepository.save(user);

    }

    /**
     * Get all users
     */
    public List<UserModel> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            // For critical operations, you can throw custom internal server error
            throw new InternalServerErrorException("Failed to fetch users: " + e.getMessage());
        }
    }

    /**
     * Get user by ID
     */
    public UserModel getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    /**
     * Update user
     */
    public UserModel updateUser(Long id, UserModel userDetails) {
        UserModel existingUser = getUserById(id);

        // Update fields if provided
        if (userDetails.getName() != null && !userDetails.getName().trim().isEmpty()) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().trim().isEmpty()) {
            if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                    userRepository.existsByEmail(userDetails.getEmail())) {
                throw new DataIntegrityViolationException("User with this email already exists");
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);

    }

    public UserModel findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserModel findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
