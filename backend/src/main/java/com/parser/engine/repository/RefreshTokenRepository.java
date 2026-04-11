package com.parser.engine.repository;

import com.parser.engine.entity.RefreshToken;
import com.parser.engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

	List<RefreshToken> findByUserAndRevokedFalse(User user);

	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
	int revokeAllUserTokens(@Param("user") User user);

	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.expiresAt < :now")
	void revokeExpiredTokens(@Param("now") Instant now);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :cutoffDate")
	void deleteExpiredTokens(@Param("cutoffDate") Instant cutoffDate);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
	int deleteByUser(@Param("user") User user);
}
