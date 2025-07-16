package com.crystal.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.crystal.store.exception.ErrorResponse;
import com.crystal.store.exception.InternalServerErrorException;
import com.crystal.store.exception.Unauthorize;
import com.crystal.store.model.UserModel;
import com.crystal.store.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTConfig jwtConfig;

    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        System.out.println("Token>>>>>>>: " + token);
        if (token != null) {
            String data = jwtConfig.extractDataFromToken(token);
            if (jwtConfig.validateToken(token)) {
                try {
                    UserModel user = userService.findByEmail(data);
                    if (user != null) {
                        // Create authentication object
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null,
                                Collections.singletonList(new SimpleGrantedAuthority(user.getUserType().name())));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    throw new InternalServerErrorException("JWT Authentication failed: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Unauthorized Access>>>>>>>>>>>>>>>>>>>");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ErrorResponse errorResponse1 = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication required");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse1));
            // Don't continue the filter chain for unauthorized requests
            return;
        }
    }
}
