package com.microservices.identity_service.service.impl;



import com.microservices.identity_service.dto.response.TokenValidationResponse;
import com.microservices.identity_service.exception.wrapper.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import com.microservices.identity_service.model.User;
import com.microservices.identity_service.service.JwtService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;


//JwtService is responsible for handling JWT (JSON Web Token) operations
// such as token generation, extraction of claims, and token validation.

@Service
public class JwtServiceImpl implements JwtService {
    // Secret Key for signing the JWT. It should be kept private.
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Value("${application.security.jwt.cookie-name}")
    private String jwtCookieName;

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generates a JWT token for the given userName.
    @Override
    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return buildToken(extraClaims, userDetails,jwtExpiration);
    }

    @Override
    public ResponseCookie generateJwtCookie(String jwt) {
        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/")
                .maxAge(24 * 60 * 60) // 24 hours
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    @Override
    public TokenValidationResponse validateToken(String token, String expectedUsername) {
//        if (secretKey == null || secretKey.isEmpty())
//            throw new IllegalArgumentException("Not found secret key in structure");
//
//        if (token.startsWith("Bearer "))
//            token = token.replace("Bearer ", "");
//
//        try {
//            final Claims claims = extractAllClaims(token);
////            long currentTimeMillis = System.currentTimeMillis();
//            // 2. Extract username from token
//            String usernameInToken = claims.getSubject();
//            // 3. Check if token is expired
//            boolean isExpired = claims.getExpiration().before(new Date());
//            // 4. Combined Validation: User matches AND token is not expired
//            return (usernameInToken.equals(expectedUsername) && !isExpired);
//
//        } catch (ExpiredJwtException ex) {
//            throw new IllegalArgumentException("Token has expired.");
//        } catch (MalformedJwtException ex) {
//            throw new IllegalArgumentException("Invalid token.");
//        } catch (SignatureException ex) {
//            throw new IllegalArgumentException("Token validation error.");
//        } catch (IllegalArgumentException ex) {
//            throw new IllegalArgumentException("Token validation error: " + ex.getMessage());
//        }

        // 1. Structural Validation
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT Secret Key is not configured in the application properties.");
        }

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is missing.");
        }

        // 2. Clean the token string
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            final Claims claims = extractAllClaims(token);

            String usernameInToken = claims.getSubject();
            boolean isExpired = claims.getExpiration().before(new Date());

            // 3. Logic Validation (Username & Expiration)
            if (!usernameInToken.equals(expectedUsername)) {
                throw new JwtAuthenticationException("Token username does not match the expected user.");
            }

            if (isExpired) {
                throw new JwtAuthenticationException("Token has expired.");
            }

            // 4. Success Response Object
            return new TokenValidationResponse("valid Token",
                    usernameInToken,
                    "ACTIVE",
                    claims.getIssuedAt().toString(),
                    claims.getExpiration().toString()
            );

        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Token has expired.");
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException ex) {
            throw new JwtAuthenticationException("Invalid token signature or format.");
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Token validation error: " + ex.getMessage());
        }

    }

    @Override
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    @Override
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
    }

    // Build JWT token with claims, subject, issued time, expiration time, and signing algorithm
    // Token valid for 3 minutes
    private String buildToken(
            Map<String, Object> extraClaims,
            User userDetails,
            long expiration
    ) {

//        Add roles/authorities to claims
        extraClaims.put("authorities", userDetails.getRole().getAuthorities());

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(),Jwts.SIG.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String reduceTokenExpiration(String token) {
        // Decode the token to extract its claims
        Claims claims = extractAllClaims( token);
        // Reduce the expiration time by setting it to a past date

        Date now = new Date();
        Date expiredAt = new Date(now.getTime() - 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(claims.getSubject())
                .issuedAt(now)
                .expiration(expiredAt)
                .signWith(getSigningKey(),Jwts.SIG.HS256)
                .compact();

    }

}

