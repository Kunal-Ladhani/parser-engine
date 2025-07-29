package com.parser.engine.utils;

import com.parser.engine.config.JwtConfig;
import com.parser.engine.dto.response.TokenResponseDto;
import com.parser.engine.entity.RefreshToken;
import com.parser.engine.entity.User;
import com.parser.engine.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtils {

	private final JwtConfig jwtConfig;
	private final RefreshTokenService refreshTokenService;

	@Autowired
	public JwtUtils(JwtConfig jwtConfig, RefreshTokenService refreshTokenService) {
		this.jwtConfig = jwtConfig;
		this.refreshTokenService = refreshTokenService;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		try {
			final Claims claims = extractAllClaims(token);
			return claimsResolver.apply(claims);
		} catch (JwtException e) {
			log.error("Failed to extract claim from token: {}", e.getMessage());
			return null;
		}
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public SecretKey getSigningKey() {
		String secret = jwtConfig.getSecret();
		byte[] decode = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(decode);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		try {
			final String username = extractUsername(token);
			return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		} catch (Exception e) {
			log.error("Token validation failed for user {}: {}", userDetails.getUsername(), e.getMessage());
			return false;
		}
	}

	public boolean isTokenExpired(String token) {
		try {
			Date expiration = extractExpiration(token);
			return expiration != null && expiration.before(new Date());
		} catch (Exception e) {
			log.error("Failed to check token expiration: {}", e.getMessage());
			return true; // Consider expired if we can't determine
		}
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", user.getId().toString());
		claims.put("username", user.getUsername());
		claims.put("role", user.getRole().name());
		return createToken(claims, user.getEmail());
	}

	public TokenResponseDto generateTokenResponse(User user) {
		// Generate access token
		String accessToken = generateToken(user);

		// Generate refresh token
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

		return TokenResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken.getToken())
				.tokenType("Bearer")
				.expiresIn(jwtConfig.getExpiration() / 1000) // Convert to seconds
				.username(user.getUsername())
				.email(user.getEmail())
				.role(user.getRole().name())
				.build();
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", userDetails.getAuthorities());
		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

		return Jwts.builder()
				.claims(claims)
				.subject(subject)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}

	public UUID extractUserId(String token) {
		String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
		return userIdStr != null ? UUID.fromString(userIdStr) : null;
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public boolean validateToken(String token) {
		try {
			extractAllClaims(token);
			return !isTokenExpired(token);
		} catch (SecurityException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

}
