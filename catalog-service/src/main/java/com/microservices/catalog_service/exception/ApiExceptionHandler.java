package com.microservices.catalog_service.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.microservices.catalog_service.exception.payload.ExceptionMessage;
import com.microservices.catalog_service.exception.wrapper.CategoryNotFoundException;
import com.microservices.catalog_service.exception.wrapper.CatalogNotFoundException;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Hidden
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
    })
    public <T extends BindException> ResponseEntity<ExceptionMessage> handleValidationException(final T e) {
        log.info("**ApiExceptionHandler controller, handle validation exception*\n");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .message("*" + Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage() + "!**")
                        .httpStatus(badRequest)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(), badRequest);
    }

    @ExceptionHandler(value = {CategoryNotFoundException.class, CatalogNotFoundException.class,})
    public <T extends RuntimeException> ResponseEntity<ExceptionMessage> handleApiRequestException(final T e) {
        log.info("**ApiExceptionHandler controller, handle API request*\n");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .message("#### " + e.getMessage() + "! ####")
                        .httpStatus(badRequest)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(), badRequest);
    }



}

