package com.microservices.identity_service.service;

import org.apache.kafka.common.security.auth.Login;

import com.microservices.identity_service.dto.request.AuthRequest;
import com.microservices.identity_service.dto.request.ChangePasswordRequest;
import com.microservices.identity_service.dto.request.SignUp;
import com.microservices.identity_service.dto.request.UserDto;
import com.microservices.identity_service.dto.response.AuthenticationResponse;
import com.microservices.identity_service.model.User;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import java.util.Optional;

public interface AuthService {
    // public AuthenticationResponse saveUser(UserRegisterRequest user);
    // public AuthenticationResponse updateUser(Long userId,UserRegisterRequest user);
    // public AuthenticationResponse authenticate(AuthRequest request);
    // public AuthenticationResponse generateAuthResonse(User user);

    User register(SignUp signUp);
    AuthenticationResponse login(AuthRequest signInForm);
    Void logout();
    User update(Long userId, SignUp update);
    String changePassword(ChangePasswordRequest request);
    // //    Mono<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
    String delete(Long id);
    Optional<User> findById(Long userId);
    Optional<User> findByUsername(String userName);
    Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder);

}

