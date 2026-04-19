package com.parser.engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    @NotBlank(message = "JWT secret key cannot be blank")
    private String secret;

    @Positive(message = "JWT expiration must be positive")
    private long expirationSeconds = 900L; // 15 minutes in seconds (default)

    private String tokenPrefix = "Bearer ";

    private String headerString = "Authorization";

    // For token blacklist management (future enhancement)
    private boolean enableTokenBlacklist = false;

    // For refresh token support
    @Positive(message = "Refresh token expiration must be positive")
    private long refreshTokenExpirationSeconds = 259200L; // 3 days in seconds

    // Convenience methods to get milliseconds (for internal use)
    public long getExpiration() {
        return expirationSeconds * 1000;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpirationSeconds * 1000;
    }
}
