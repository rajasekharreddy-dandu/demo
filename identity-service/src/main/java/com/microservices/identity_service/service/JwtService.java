package com.microservices.identity_service.service;


import com.microservices.identity_service.dto.response.TokenValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

import com.microservices.identity_service.model.User;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(User userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
    TokenValidationResponse validateToken(String token, String expectedUsername);
    ResponseCookie generateJwtCookie(String jwt);
    String getJwtFromCookies(HttpServletRequest request);
    ResponseCookie getCleanJwtCookie();
    String reduceTokenExpiration(String token);
}

