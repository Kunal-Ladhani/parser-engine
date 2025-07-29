package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.config.JwtConfig;
import com.parser.engine.entity.RefreshToken;
import com.parser.engine.entity.User;
import com.parser.engine.exception.ResourceDoesNotExistsException;
import com.parser.engine.repository.RefreshTokenRepository;
import com.parser.engine.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtConfig jwtConfig;

	@Autowired
	public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtConfig jwtConfig) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtConfig = jwtConfig;
	}

	@Override
	@Transactional
	public RefreshToken createRefreshToken(User user) {
		// Generate a secure random token
		String tokenValue = UUID.randomUUID().toString();

		// Calculate expiration time
		Instant expiresAt = Instant.now().plusMillis(jwtConfig.getRefreshTokenExpiration());

		// Create refresh token
		RefreshToken refreshToken = new RefreshToken(tokenValue, expiresAt, user);

		// Save to database
		RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
		log.info("Created refresh token for user: {}", user.getEmail());

		return savedToken;
	}

	@Override
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.isExpired()) {
			log.error("Refresh token expired: {}", token.getToken());
			refreshTokenRepository.delete(token);
			throw new ResourceDoesNotExistsException(ExceptionCode.A103, "Refresh token was expired. Please make a new signin request");
		}

		if (token.isRevoked()) {
			log.error("Refresh token was revoked: {}", token.getToken());
			throw new ResourceDoesNotExistsException(ExceptionCode.A104, "Refresh token was revoked. Please make a new signin request");
		}

		return token;
	}

	@Override
	public RefreshToken findByToken(String token) {
		return refreshTokenRepository.findByTokenAndRevokedFalse(token)
				.orElseThrow(() -> {
					log.error("Refresh token not found: {}", token);
					return new ResourceDoesNotExistsException(ExceptionCode.A104, "Refresh token not found");
				});
	}

	@Override
	@Transactional
	public void revokeToken(RefreshToken token) {
		token.setRevoked(true);
		refreshTokenRepository.save(token);
		log.info("Revoked refresh token: {}", token.getToken());
	}

	@Override
	@Transactional
	public int revokeAllUserTokens(User user) {
		int revokedCount = refreshTokenRepository.revokeAllUserTokens(user);
		log.info("Revoked {} refresh tokens for user: {}", revokedCount, user.getEmail());
		return revokedCount;
	}

	@Override
	@Transactional
	public void cleanupExpiredTokens() {
		Instant now = Instant.now();
		refreshTokenRepository.deleteExpiredTokens(now);
		log.info("Cleaned up expired refresh tokens");
	}
}
