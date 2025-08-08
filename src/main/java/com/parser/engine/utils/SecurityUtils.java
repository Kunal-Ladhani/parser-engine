package com.parser.engine.utils;

import com.parser.engine.entity.User;
import com.parser.engine.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class SecurityUtils {

	private static UserRepository userRepository;
	private static JwtUtils jwtUtils;

	@Autowired
	public SecurityUtils(UserRepository userRepository, JwtUtils jwtUtils) {
		SecurityUtils.userRepository = userRepository;
		SecurityUtils.jwtUtils = jwtUtils;
	}

	private static Authentication getCurrentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * Get the current logged-in user's email from JWT token
	 * The email is stored in the "sub" field of the JWT and also as the username in SecurityContext
	 */
	public static String getLoggedInUserEmail() {
		try {
			Authentication authentication = getCurrentAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				// The username in UserDetails is actually the email (see UserDetailsConfig)
				String email = authentication.getName();
				log.debug("Retrieved logged-in user email: {}", email);
				return email;
			}
			log.warn("No authenticated user found in security context");
			return null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in user email: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Get the current logged-in user entity from database
	 */
	public static User getLoggedInUser() {
		try {
			String email = getLoggedInUserEmail();
			if (email != null) {
				return userRepository.findByEmail(email).orElse(null);
			}
			return null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in user: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Get the current logged-in user's username from the User entity
	 */
	public static String getLoggedInUsername() {
		try {
			User user = getLoggedInUser();
			return user != null ? user.getUsername() : null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in username: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Get the current logged-in user's first name
	 */
	public static String getLoggedInUserFirstName() {
		try {
			User user = getLoggedInUser();
			return user != null ? user.getFirstName() : null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in user first name: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Get the current logged-in user's last name
	 */
	public static String getLoggedInUserLastName() {
		try {
			User user = getLoggedInUser();
			return user != null ? user.getLastName() : null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in user last name: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Get the current logged-in user's ID from JWT token
	 */
	public static UUID getLoggedInUserId() {
		try {
			// We can also extract from JWT token for better performance
			String email = getLoggedInUserEmail();
			if (email != null) {
				User user = userRepository.findByEmail(email).orElse(null);
				return user != null ? user.getId() : null;
			}
			return null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in user ID: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Get the current logged-in user's role
	 */
	public static String getLoggedInUserRole() {
		try {
			User user = getLoggedInUser();
			return user != null ? user.getRole().name() : null;
		} catch (Exception e) {
			log.error("Error retrieving logged-in user role: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Check if user is authenticated
	 */
	public static boolean isAuthenticated() {
		try {
			Authentication authentication = getCurrentAuthentication();
			return authentication != null && authentication.isAuthenticated() &&
					!"anonymousUser".equals(authentication.getName());
		} catch (Exception e) {
			log.error("Error checking authentication status: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Check if the current user has a specific role
	 */
	public static boolean hasRole(String role) {
		try {
			String userRole = getLoggedInUserRole();
			return userRole != null && userRole.equals(role);
		} catch (Exception e) {
			log.error("Error checking user role: {}", e.getMessage());
			return false;
		}
	}
}
