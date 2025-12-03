
package com.microservices.identity_service.exception.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservices.identity_service.constant.AppConstant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String timestamp;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Throwable throwable;
    private Object message;
    private String path;
}