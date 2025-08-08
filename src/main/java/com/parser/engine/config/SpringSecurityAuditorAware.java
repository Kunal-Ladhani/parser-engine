package com.parser.engine.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuditorAware to capture the current auditor for JPA
 * auditing. Directly uses Spring Security context to avoid circular
 * dependencies.
 */
@Slf4j
@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null
                    && authentication.isAuthenticated()
                    && authentication.getName() != null
                    && !"anonymousUser".equals(authentication.getName())) {

                // The authentication name is the email (as configured in UserDetailsService)
                String authenticatedUser = authentication.getName();
                return Optional.of(authenticatedUser);
            }

            // Fallback for system operations or when no user is authenticated
            return Optional.of("system");
        } catch (Exception e) {
            // Fallback to system in case of any errors
            log.error("Error getting current auditor: {}", e.getMessage());
            return Optional.of("system");
        }
    }
}
