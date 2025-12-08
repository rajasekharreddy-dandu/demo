package com.microservices.identity_service.controller;

import com.microservices.identity_service.dto.response.*;
import com.microservices.identity_service.enums.TokenType;
import com.microservices.identity_service.model.User;
import com.microservices.identity_service.utils.ControllerHelper;
import com.microservices.identity_service.validate.AuthorityTokenUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.microservices.identity_service.dto.request.AuthRequest;
import com.microservices.identity_service.dto.request.RefreshTokenRequest;
import com.microservices.identity_service.dto.request.SignUp;
// import com.microservices.identity_service.dto.request.UserRegisterRequest;
import com.microservices.identity_service.service.AuthService;
import com.microservices.identity_service.service.JwtService;
import com.microservices.identity_service.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Authentication API", description = "APIs for user registration, login, and authentication")
public class AuthController {
    // @Autowired
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final ControllerHelper controllerHelper;

    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Client Side Error")

    })
    @PostMapping({"/register"})//"/signup",
    public ResponseEntity<?> register(@Valid @RequestBody SignUp signUp) {
        User user=authService.register(signUp);
        log.info("User {} created successfully.",user.getUsername());
        return ResponseEntity.ok(controllerHelper.getUserResonse(user,true));

    }
    @Operation(summary = "User login", description = "Logs in a user with the provided credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping({ "/login"})//"/signin",
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthRequest signInForm) {

        AuthenticationResponse response = authService.login(signInForm); // synchronous call
        return ResponseEntity.ok(response);
    }


    // @PostMapping("/register")
    // public ResponseEntity<AuthenticationResponse> register(@RequestBody UserRegisterRequest request) {
    //     AuthenticationResponse authenticationResponse = authService.saveUser(request);
    //     ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
    //     ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
    //     return ResponseEntity.ok()
    //             .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
    //             .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
    //             .body(authenticationResponse);
    // }

    // @PostMapping("/authenticate")
    // public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthRequest request) {
    //     AuthenticationResponse authenticationResponse = authService.authenticate(request);
    //     ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
    //     ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
    //     return ResponseEntity.ok()
    //             .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
    //             .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
    //             .body(authenticationResponse);
    // }
    // @PutMapping("/user/{id}")
    // public ResponseEntity<AuthenticationResponse> register(@PathVariable("id") Long userId ,@RequestBody UserRegisterRequest request) {
    //     AuthenticationResponse authenticationResponse = authService.updateUser(userId,request);
    //     ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
    //     ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());
    //     return ResponseEntity.ok()
    //             .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
    //             .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
    //             .body(authenticationResponse);
    // }

    @Operation(summary = "User logout", description = "Logs out the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")

    })
    @PostMapping("/user/logout")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<String> logout() {
        log.info("Logout endpoint called");
        try {
            authService.logout(); // synchronous method
            return ResponseEntity.ok("Logged out successfully.");
        } catch (Exception e) {
            log.error("Logout failed", e);
            return ResponseEntity.badRequest().body("Logout failed.");
        }
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }

    @PostMapping("/refresh-token-cookie")
    public ResponseEntity<Void> refreshTokenCookie(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService
                .generateNewToken(new RefreshTokenRequest(refreshToken));
        ResponseCookie NewJwtCookie = jwtService.generateJwtCookie(refreshTokenResponse.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, NewJwtCookie.toString())
                .build();
    }
    @GetMapping("/info")
    public Authentication getAuthentication(@RequestBody AuthRequest request){
        return  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        if(refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }
        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .build();

    }

    @Operation(summary = "Check user authority", description = "Checks if the user has the specified authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role access API"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    @GetMapping({"/hasAuthority", "/authorization"})
    public Boolean getAuthority(@RequestHeader(name = "Authorization") String authorizationToken,
                                String requiredRole) {
        AuthorityTokenUtil authorityTokenUtil = new AuthorityTokenUtil();
        List<String> authorities = authorityTokenUtil.checkPermission(authorizationToken);

        if (authorities.contains(requiredRole)) {
            return ResponseEntity.ok(new TokenValidationResponse("Role access api")).hasBody();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token")).hasBody();
        }
    }

    @Operation(summary = "Validate JWT token", description = "Validates the provided JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is Valid"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping({"/validate-token"})
    public Boolean validateToken(@RequestHeader(name = "Authorization") String authorizationToken) {

        if (jwtService.validateToken(authorizationToken)) {
            return ResponseEntity.ok(new TokenValidationResponse("Valid token")).hasBody();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenValidationResponse("Invalid token")).hasBody();
        }
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
}

