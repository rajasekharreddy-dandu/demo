package com.microservices.identity_service.exception;

import com.microservices.identity_service.enums.Role;
import com.microservices.identity_service.exception.payload.ErrorResponse;
//import com.microservices.identity_service.exception.payload.ExceptionMessage;
import com.microservices.identity_service.exception.wrapper.EmailOrUsernameNotFoundException;
import com.microservices.identity_service.exception.wrapper.PasswordNotFoundException;
import com.microservices.identity_service.exception.wrapper.PhoneNumberNotFoundException;
import com.microservices.identity_service.exception.wrapper.TokenException;
import com.microservices.identity_service.exception.wrapper.UserNotAuthenticatedException;
import com.microservices.identity_service.exception.wrapper.UserNotFoundException;
import com.microservices.identity_service.exception.wrapper.RoleNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.BindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiControllerHandler {

    @ExceptionHandler(value = TokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(TokenException ex, WebRequest request){
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .error("Invalid Token")
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, ServerWebExchange exchange) {
//        Map<String, String> fieldErrors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                fieldErrors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setError("Validation Failed");
//        errorResponse.setTimestamp(Instant.now());
//        errorResponse.setMessage("Invalid input");
//        errorResponse.setPath(exchange.getRequest().getPath().value());
//        errorResponse.setThrowable(ex);
//
//        return ResponseEntity.badRequest().body(fieldErrors);
//    }



    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>("Cannot parse JSON :: accepted roles "+ Arrays.toString(Role.values()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class
    })
    public <T extends BindException> ResponseEntity<ErrorResponse> handleValidationException(final T e,  WebRequest request) {
        log.info("ApiExceptionHandler:  Handle validation exception\n");
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .error("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .message(fieldErrors)
                .path(request.getDescription(false))
//                 .throwable(e)
                .build();
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            UserNotFoundException.class,
            RoleNotFoundException.class,
            PasswordNotFoundException.class,
            EmailOrUsernameNotFoundException.class,
            PhoneNumberNotFoundException.class
    })
    public <T extends RuntimeException> ResponseEntity<ErrorResponse> handleApiRequestException(final T e,  WebRequest request) {
        log.info("ApiExceptionHandler controller, handle API request\n");
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .error("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .message( e.getMessage())
                .path(request.getDescription(false))
//                .throwable(e)
                .build();
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AccessDeniedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex,WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .error("Authorization Failed")
                .status(HttpStatus.FORBIDDEN.value())
                .message( "Access denied: " + ex.getMessage())
                .path(request.getDescription(false))
//                .throwable(e)
                .build();
        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);
    }

//    @ExceptionHandler(WebExchangeBindException.class)
//    public Mono<ResponseEntity<Map<String, String>>> handleValidationErrors(WebExchangeBindException ex) {
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getFieldErrors().forEach(error -> {
//            errors.put(error.getField(), error.getDefaultMessage());
//        });
//
//        return Mono.just(ResponseEntity.badRequest().body(errors));
//    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,WebRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .error("Authorization Failed")
                .status(HttpStatus.UNAUTHORIZED.value())
                .message( "Authentication failed: " + ex.getMessage())
//                .path(request.getDescription(false))
//                .throwable(e)
                .build();
        return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<String> handleUserNotAuthenticatedException(UserNotAuthenticatedException ex) {
        log.error("User not authenticated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }

}

