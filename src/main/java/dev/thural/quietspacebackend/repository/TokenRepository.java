package dev.thural.quietspacebackend.repository;

import dev.thural.quietspacebackend.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    boolean existsByJwtToken(String jwtToken);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    TokenEntity getByEmail(String email);
}
