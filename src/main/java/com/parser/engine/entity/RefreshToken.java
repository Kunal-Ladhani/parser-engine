package com.parser.engine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "revoked", nullable = false)
	private boolean revoked = false;

	public RefreshToken(String token, Instant expiresAt, User user) {
		this.token = token;
		this.expiresAt = expiresAt;
		this.user = user;
		this.createdAt = Instant.now();
	}

	public boolean isExpired() {
		return Instant.now().isAfter(this.expiresAt);
	}

	public boolean isActive() {
		return !revoked && !isExpired();
	}
}
