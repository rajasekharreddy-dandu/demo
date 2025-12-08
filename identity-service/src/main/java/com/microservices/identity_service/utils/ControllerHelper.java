package com.microservices.identity_service.utils;

import com.microservices.identity_service.dto.response.AuthenticationResponse;
import com.microservices.identity_service.dto.response.InformationMessage;
import com.microservices.identity_service.enums.TokenType;
import com.microservices.identity_service.model.User;
import com.microservices.identity_service.service.AuthService;
import com.microservices.identity_service.service.JwtService;
import com.microservices.identity_service.service.RefreshTokenService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControllerHelper {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;


    public  AuthenticationResponse getUserResonse(User user,boolean edit){

        String jwt = null;
        String refreshToken = null;
        if (edit) { // Or whatever condition you need
            jwt = jwtService.generateToken(user);
            refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
        }
        var roles = user.getRole().getAuthorities();
//                .stream()
//                .map(SimpleGrantedAuthority::getAuthority)
//                .toList();

        var userInfo= InformationMessage.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .roles(roles)
                .build();

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER.name())
                .userInfo(userInfo)
                .build();
    }
}
