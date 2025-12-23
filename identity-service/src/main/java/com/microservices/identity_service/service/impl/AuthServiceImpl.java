package com.microservices.identity_service.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.microservices.identity_service.dto.request.*;
import com.microservices.identity_service.exception.wrapper.*;
import com.microservices.identity_service.security.UserDetailsServices;
import com.microservices.identity_service.security.UserPrinciple;
import com.microservices.identity_service.utils.ControllerHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.identity_service.model.User;
// import com.microservices.identity_service.dto.request.UserRegisterRequest;
import com.microservices.identity_service.dto.response.AuthenticationResponse;
import com.microservices.identity_service.dto.response.InformationMessage;
import com.microservices.identity_service.enums.TokenType;
import com.microservices.identity_service.repository.UserRepository;
import com.microservices.identity_service.service.JwtService;
import com.microservices.identity_service.service.RefreshTokenService;
import com.microservices.identity_service.service.AuthService;
// import com.microservices.identity_service.exception.UserNotFoundException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    @Autowired
    private UserRepository userRepository;
    //     @Autowired
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsServices userDetailsService;



//     @Override
//     public AuthenticationResponse authenticate(AuthRequest request) {
//         System.out.println(request);
//         Authentication authenticate = authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
//         System.out.println("Authenticate "+authenticate);
//         var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
//         System.out.println("User details: "+user);
//         return generateAuthResonse(user);
//     }

    @Override
    public User register(SignUp signUp) {
        log.info("Authservice, Register new user" );
        if (existsByUsername(signUp.getUsername())) {
            throw new EmailOrUsernameNotFoundException("The username " + signUp.getUsername() + " is existed, please try again.");
        }
        if (existsByEmail(signUp.getEmail())) {
            throw new EmailOrUsernameNotFoundException("The email " + signUp.getEmail() + " is existed, please try again.");
        }
        if (existsByPhoneNumber(signUp.getPhone())) {
            throw new PhoneNumberNotFoundException("The phone number " + signUp.getPhone() + " is existed, please try again.");
        }

        User user = modelMapper.map(signUp, User.class);
        user.setPassword(passwordEncoder.encode(signUp.getPassword()));
        user.setRole(signUp.getRole());
        // user.setRoles(signUp.getRoles()
        //         .stream()
        //         .map(role -> roleService.findByName(mapToRoleName(role))
        //                 .orElseThrow(() -> new RuntimeException("Role not found in the database.")))
        //         .collect(Collectors.toSet()));
        log.info("New user is registered : {}",user );
        user =userRepository.save(user);

        return user;

    }

    @Override
    public AuthenticationResponse login(AuthRequest signInForm) {
        log.info("Authservice, Login user" );
        String usernameOrEmail = signInForm.getUsername();
        boolean isEmail = usernameOrEmail.contains("@gmail.com");

        UserDetails userDetails;
        if (isEmail) {
            log.info("Authservice, Login user by email" );
            userDetails = userDetailsService.loadUserByEmail(usernameOrEmail);
        } else {
            log.info("Authservice, Login user by username" );
            userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
        }

        // check username
        if (userDetails == null) {
            throw new UserNotFoundException("User not found");
        }

        // Check password
        if (!passwordEncoder.matches(signInForm.getPassword(), userDetails.getPassword())) {
            throw new PasswordNotFoundException("Incorrect password");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                signInForm.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) userDetails;
        User user=new User();
        BeanUtils.copyProperties(userPrinciple, user);

        System.out.println("User details: "+userPrinciple);
        return  generateAuthResonse(user);

    }



    @Transactional
    @Override
    public User update(Long id, SignUp updateDTO) {
        log.info("AuthService, Updated User details" );

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found userId: " + id + " for update"));
        // 2a. Validate Username Uniqueness
        String newUsername = updateDTO.getUsername();
        if (newUsername != null && !newUsername.equals(existingUser.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(user -> {
                throw new DuplicateUserException("Username is already taken: " + newUsername);
            });
        }

        // 2b. Validate Email Uniqueness
        String newEmail = updateDTO.getEmail();
        if (newEmail != null && !newEmail.equals(existingUser.getEmail())) {
            userRepository.findByEmail(newEmail).ifPresent(user -> {
                throw new DuplicateUserException("Email is already taken: " + newEmail);
            });
        }

        // 2c. Validate Phone Number Uniqueness
        String newPhone = updateDTO.getPhone();
        if (newPhone != null && !newPhone.equals(existingUser.getPhone())) {
            userRepository.findByPhone(newPhone).ifPresent(user -> {
                throw new DuplicateUserException("Phone number is already taken: " + newPhone);
            });
        }



//        modelMapper.map(updateDTO, existingUser);
        BeanUtils.copyProperties(updateDTO, existingUser,"id", "password", "createdAt");
//        if(updateDTO.getPassword()!=null) {
//            existingUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
//        }
//        existingUser.setRole(updateDTO.getRole());

        return userRepository.save(existingUser);

    }

    @Override
    public Void logout() {
        log.info("AuthService: Logout user" );
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SecurityContextHolder.getContext().setAuthentication(null);

        String currentToken = getCurrentToken();

        if (authentication != null && authentication.isAuthenticated()) {
            // Invalidate the current token by reducing its expiration time
            String updatedToken = jwtService.reduceTokenExpiration(currentToken);
        }

        SecurityContextHolder.clearContext();
        return null;

    }

    @Override
    public String changePassword(ChangePasswordRequest request) {
        log.info("AuthService: changePassword" );
        try{
            UserDetails userDetails = getCurrentUserDetails();
            String username = userDetails.getUsername();

            User existingUser = findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));

            if (passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
                if (validateNewPassword(request.getNewPassword(), request.getConfirmPassword())) {
                    existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(existingUser);

                    // send email through kafka client
                    EmailDetails emailDetails = emailDetailsConfig(username);

//                    return eventProducer.send(KafkaConstant.PROFILE_ONBOARDING_TOPIC, gson.toJson(emailDetails))
//                            .thenReturn("Password changed successfully")
//                            .publishOn(Schedulers.boundedElastic());
                }

                return "Password changed failed.";
            } else {
                throw new PasswordNotFoundException("Incorrect password");
            }
        } catch (Exception e) {
            throw new UserNotAuthenticatedException("Transaction silently rolled back");
        }
    }

    @Override
    public String delete(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> {
                            try {
                                userRepository.delete(user);
                            } catch (DataAccessException e) {
                                throw new RuntimeException("Error deleting user with userId: " + id, e);
                            }
                        },
                        () -> {
                            throw new UserNotFoundException("User not found for userId: " + id);
                        }
                );
        return "User with id " + id + " deleted successfully.";
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.of(userRepository.findById(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String userName) {
        return Optional.ofNullable(userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with userName: " + userName)));
    }

    @Override
    public Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageRequest);

        return usersPage.map(user -> modelMapper.map(user, UserDto.class));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phone) {
        return userRepository.existsByPhoneNumber(phone);
    }

    public AuthenticationResponse generateAuthResonse(User user){
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        // System.out.println(jwt);
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
                .refreshToken(refreshToken.getToken())
                .tokenType(TokenType.BEARER.name())
                .userInfo(userInfo)
                .build();
    }

    private String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();
            if (credentials instanceof String) {
                return (String) credentials;
            }
        }

        return null;
    }

    private EmailDetails emailDetailsConfig(String username) {
        return EmailDetails.builder()
                .recipient("rajasekhartest@gmail.com")
                .msgBody(textSendEmailChangePasswordSuccessfully(username))
                .subject("Password Change Successful: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .attachment("Please be careful, don't let this information leak")
                .build();
    }

    public String textSendEmailChangePasswordSuccessfully(String username) {
        return "Hey " + username + "!\n\n" +
                "This is a confirmation that your password has been successfully changed.\n" +
                " If you did not initiate this change, please contact our support team immediately.\n" +
                "If you have any questions or concerns, feel free to reach out to us.\n\n" +
                "Best regards:\n\n" +
                "Contact: hoangtien2k3qx1@gmail.com\n" +
                "Fanpage: https://hoangtien2k3qx1.github.io/";
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            throw new UserNotAuthenticatedException("User not authenticated.");
        }
    }

    private boolean validateNewPassword(String newPassword, String confirmPassword) {
        return Objects.equals(newPassword, confirmPassword);
    }
}

