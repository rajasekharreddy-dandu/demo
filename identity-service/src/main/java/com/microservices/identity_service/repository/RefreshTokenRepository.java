package com.microservices.identity_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.microservices.identity_service.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

}


