package com.parser.engine.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration for registering interceptors, CORS, and other web-related
 * configurations
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final String[] ALLOWED_ORIGIN = new String[]{
			"http://localhost:5173", // React dev server
			"https://*.yourdomain.com" // production domains
	};

	private static final String[] ALLOWED_METHODS = new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};

	private static final String[] ALLOWED_HEADERS = new String[]{"*"};

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// Main API endpoints - full CORS support
		registry.addMapping("/parser-engine/auth/**")
				.allowedOriginPatterns(ALLOWED_ORIGIN)
				.allowedMethods(ALLOWED_METHODS)
				.allowedHeaders(ALLOWED_HEADERS)
				.allowCredentials(true)
				.maxAge(3600);

		registry.addMapping("/parser-engine/wb/**")
				.allowedOriginPatterns(ALLOWED_ORIGIN)
				.allowedMethods(ALLOWED_METHODS)
				.allowedHeaders(ALLOWED_HEADERS)
				.allowCredentials(true)
				.maxAge(3600);

		// Documentation endpoints - read-only CORS
		registry.addMapping("/parser-engine/swagger-ui/**")
				.allowedOriginPatterns(ALLOWED_ORIGIN)
				.allowedMethods("GET", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(false)
				.maxAge(3600);

		registry.addMapping("/parser-engine/v3/api-docs/**")
				.allowedOriginPatterns(ALLOWED_ORIGIN)
				.allowedMethods("GET", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(false)
				.maxAge(3600);

		// Error handling endpoint
		registry.addMapping("/parser-engine/error")
				.allowedOriginPatterns(ALLOWED_ORIGIN)
				.allowedMethods("GET", "POST", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);

		// Actuator endpoints - COMMENTED OUT for security
		// Uncomment only if you need monitoring dashboard access
        /*
		registry.addMapping("/parser-engine/actuator/**")
				.allowedOriginPatterns("http://localhost:5173") // Only dev environment
				.allowedMethods("GET", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(false)
				.maxAge(300); // Shorter cache for sensitive endpoints
         */
	}

}
