package com.crystal.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crystal.store.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmail(String email);

    UserModel findByUsername(String username);

    UserModel findByEmail(String email);
}
