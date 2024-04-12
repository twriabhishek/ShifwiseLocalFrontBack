package com.exato.usermodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exato.usermodule.entity.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByToken(String token);
}

