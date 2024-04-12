package com.exato.usermodule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.ClientInfo;

@Repository
public interface ClientInfoRepository extends JpaRepository<ClientInfo, Long> {
	
	public Optional<ClientInfo> findByEmail(String email);

}
