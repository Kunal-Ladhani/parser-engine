package com.parser.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(
		auditorAwareRef = "springSecurityAuditorAware",
		dateTimeProviderRef = "auditingDateTimeProvider"
)
public class JpaDateTimeConfig {

	@Bean(name = "auditingDateTimeProvider")
	public DateTimeProvider auditingDateTimeProvider() {
		return () -> Optional.of(LocalDateTime.now());
	}
}
