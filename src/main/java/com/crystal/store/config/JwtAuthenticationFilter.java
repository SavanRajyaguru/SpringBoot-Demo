package com.crystal.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.crystal.store.exception.InternalServerErrorException;
import com.crystal.store.model.UserModel;
import com.crystal.store.services.JwtService;
import com.crystal.store.services.UserService;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            Map<String, String> data = jwtService.extractDataFromToken(token);
            if (jwtService.validateToken(token)) {
                try {
                    UserModel user = userService.findByUsername(data.get("username"));
                    if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Create authentication object
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null,
                                Collections.singletonList(new SimpleGrantedAuthority(user.getUserType().name())));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    throw new InternalServerErrorException("JWT Authentication failed: " + e.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
