package com.microservices.identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationResponse {
    private String message;
   private String username;
    private String status;
    private String issuedAt;
    private String expiresAt;

    public TokenValidationResponse(String message) {
        this.message = message;
    }

}

