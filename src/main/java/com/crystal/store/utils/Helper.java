package com.crystal.store.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Helper {
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
