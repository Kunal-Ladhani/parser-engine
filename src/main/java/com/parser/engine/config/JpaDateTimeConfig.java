package com.parser.engine.config;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.parser.engine.utils.DateTimeUtils;

/**
 * JPA Configuration for consistent date/time handling in India timezone
 */
@Configuration
@EnableJpaAuditing(
        auditorAwareRef = "springSecurityAuditorAware",
        dateTimeProviderRef = "auditingDateTimeProvider"
)
public class JpaDateTimeConfig {

    /**
     * Custom DateTimeProvider that provides ZonedDateTime in India timezone for
     * JPA auditing. Returns TemporalAccessor for compatibility with Spring Data
     * JPA.
     */
    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> {
            TemporalAccessor now = DateTimeUtils.nowInIndia();
            return Optional.of(now);
        };
    }
}
