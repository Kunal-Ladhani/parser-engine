package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.request.*;
import com.parser.engine.dto.response.*;
import com.parser.engine.entity.RefreshToken;
import com.parser.engine.entity.User;
import com.parser.engine.enums.Role;
import com.parser.engine.exception.ResourceAlreadyExistsException;
import com.parser.engine.exception.ValidationException;
import com.parser.engine.repository.UserRepository;
import com.parser.engine.service.AccountDeletionService;
import com.parser.engine.service.AuthService;
import com.parser.engine.service.RefreshTokenService;
import com.parser.engine.utils.DateTimeUtils;
import com.parser.engine.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private final JwtUtils jwtUtils;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;
	private final AccountDeletionService accountDeletionService;

	@Autowired
	public AuthServiceImpl(JwtUtils jwtUtils,
						   UserRepository userRepository,
						   PasswordEncoder passwordEncoder,
						   AuthenticationManager authenticationManager,
						   RefreshTokenService refreshTokenService,
						   AccountDeletionService accountDeletionService) {
		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.refreshTokenService = refreshTokenService;
		this.accountDeletionService = accountDeletionService;
	}

	@Override
	@Transactional
	public TokenResponseDto login(LoginRequestDto loginRequest) {
		log.info("Attempting to authenticate user with loginRequest: {}", loginRequest);

		if (!loginRequest.isEmailOrUsernamePresent()) {
			throw new ValidationException(ExceptionCode.A114, ExceptionCode.A114.getDefaultMessage());
		}

		try {
			// Authenticate first - this prevents user enumeration
			var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getLoginIdentifier(), loginRequest.getPassword());
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Only get user details AFTER successful authentication
			User user = userRepository.findByEmail(authentication.getName())
					.or(() -> userRepository.findByUsername(loginRequest.getLoginIdentifier()))
					.orElseThrow(() -> {
						log.error("User account not found after authentication: {}", authentication.getName());
						return new UsernameNotFoundException("User account not found");
					});

			// Revoke existing refresh tokens for security
			refreshTokenService.revokeAllUserTokens(user);

			// Generate new tokens
			TokenResponseDto tokenResponse = jwtUtils.generateTokenResponse(user);

			log.info("User:: email: {}, username: {} is authenticated successfully with role: {}",
					user.getEmail(), user.getUsername(), user.getRole());

			return tokenResponse;

		} catch (AuthenticationException e) {
			log.error("Authentication failed for user: {}", loginRequest.getLoginIdentifier());
			throw new ValidationException(ExceptionCode.A113, "Invalid credentials");
		}
	}

	@Override
	@Transactional
	public TokenResponseDto signup(SignupRequestDto signupRequest) {
		log.info("Attempting to register new user with SignupRequestDto: {}", signupRequest);

		// Validate username uniqueness
		if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
			log.error("Username already exists: {}", signupRequest.getUsername());
			throw new ResourceAlreadyExistsException(ExceptionCode.A115, ExceptionCode.A115.getDefaultMessage());
		}

		// Validate email uniqueness
		if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
			log.error("Email already exists: {}", signupRequest.getEmail());
			throw new ResourceAlreadyExistsException(ExceptionCode.A116, ExceptionCode.A116.getDefaultMessage());
		}

		try {
			// Create new user
			User user = createNewUser(signupRequest);
			User savedUser = userRepository.save(user);

			// Generate tokens
			TokenResponseDto tokenResponse = jwtUtils.generateTokenResponse(savedUser);
			log.info("New user registered successfully: {} with role: {}", savedUser.getEmail(), savedUser.getRole());

			return tokenResponse;
		} catch (Exception e) {
			log.error("Error during user registration for {}: {}", signupRequest.getEmail(), e.getMessage());
			throw new RuntimeException("Registration failed", e);
		}
	}

	@Override
	@Transactional
	public TokenResponseDto refreshToken(String refreshRequestHeader, String authorizationHeader) {
		log.info("Processing secure refresh token request");

		// Validate refresh token input
		if (Objects.isNull(refreshRequestHeader) || refreshRequestHeader.trim().isEmpty()) {
			throw new ValidationException(ExceptionCode.A104, "Refresh token is required");
		}

		// Validate authorization header (access token)
		if (Objects.isNull(authorizationHeader) || authorizationHeader.trim().isEmpty()) {
			throw new ValidationException(ExceptionCode.A104, "Access token is required for refresh");
		}

		// Extract access token from Authorization header
		String accessToken = null;
		if (authorizationHeader.startsWith("Bearer ")) {
			accessToken = authorizationHeader.substring(7);
		} else {
			throw new ValidationException(ExceptionCode.A104, "Invalid authorization header format");
		}

		// Validate access token format and extract user info
		String accessTokenUsername = jwtUtils.extractUsername(accessToken);
		if (accessTokenUsername == null) {
			throw new ValidationException(ExceptionCode.A104, "Invalid access token");
		}

		// Check if access token is expired (should be expired for refresh to be needed)
		if (!jwtUtils.isTokenExpired(accessToken)) {
			throw new ValidationException(ExceptionCode.A121, ExceptionCode.A121.getDefaultMessage());
		}

		// Find and verify refresh token
		RefreshToken refreshToken = refreshTokenService.findByToken(refreshRequestHeader);
		refreshTokenService.verifyExpiration(refreshToken);

		User user = refreshToken.getUser();

		// SECURITY: Verify that refresh token belongs to the same user as access token
		if (!user.getEmail().equals(accessTokenUsername)) {
			log.error("Refresh token mismatch: refresh token user {} IS NOT SAME AS access token user {}", user.getEmail(), accessTokenUsername);
			throw new ValidationException(ExceptionCode.A122, ExceptionCode.A122.getDefaultMessage());
		}

		// Revoke the used refresh token (rotation)
		refreshTokenService.revokeToken(refreshToken);

		// Generate new tokens
		TokenResponseDto tokenResponse = jwtUtils.generateTokenResponse(user);

		log.info("Secure refresh token successful for user: {}", user.getEmail());
		return tokenResponse;
	}

	@Override
	@Transactional
	public LogoutResponseDto logout() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = null;
		String email = null;
		int revokedTokensCount = 0;

		try {
			if (auth != null && auth.getName() != null) {
				// Find user and get details before logout
				User user = userRepository.findByEmail(auth.getName()).orElse(null);
				if (user != null) {
					username = user.getUsername();
					email = user.getEmail();

					// Count active refresh tokens before revoking
					revokedTokensCount = refreshTokenService.revokeAllUserTokens(user);
					log.info("User: {} has been logged out. {} refresh tokens revoked.", auth.getName(), revokedTokensCount);
				}
			}

			// Clear security context
			SecurityContextHolder.clearContext();

			// Build successful logout response
			return LogoutResponseDto.builder()
					.username(username)
					.email(email)
					.message("Logout successful")
					.note(String.format("Access token remains valid for up to 15 minutes. %d refresh tokens revoked.", revokedTokensCount))
					.timestamp(DateTimeUtils.nowInIndia())
					.build();

		} catch (Exception e) {
			log.error("Error during logout: {}", e.getMessage());

			// Clear security context even if there's an error
			SecurityContextHolder.clearContext();

			// Return error response
			return LogoutResponseDto.builder()
					.username(username)
					.email(email)
					.message("Logout completed with warnings")
					.note("Some cleanup operations failed, but user session has been cleared")
					.timestamp(DateTimeUtils.nowInIndia())
					.build();
		}
	}

	private User createNewUser(SignupRequestDto signupRequest) {
		User user = new User();
		user.setUsername(signupRequest.getUsername());
		user.setEmail(signupRequest.getEmail());
		user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		user.setFirstName(signupRequest.getFirstName());
		user.setLastName(signupRequest.getLastName());
		user.setRole(Role.USER); // Default role

		log.info("Created new user entity: {}", user);
		return user;
	}

	@Override
	@Transactional
	public AccountDeletionResponseDto deleteAccount(DeleteAccountRequestDto deleteRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getName() == null) {
			throw new ValidationException(ExceptionCode.A101, "User must be authenticated to delete account");
		}

		// Find the user
		User user = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> {
					log.error("User account not found for deletion: {}", auth.getName());
					return new ValidationException(ExceptionCode.A112, "User account not found");
				});

		// Verify password
		if (!passwordEncoder.matches(deleteRequest.getCurrentPassword(), user.getPassword())) {
			log.error("Invalid password provided for account deletion: {}", user.getEmail());
			throw new ValidationException(ExceptionCode.A117, ExceptionCode.A117.getDefaultMessage());
		}

		log.info("Starting account deletion for user: {}", user.getEmail());

		// Track deletion statistics
		int refreshTokensDeleted = 0;
		int filesDeleted = 0;
		int propertiesDeleted = 0;
		int filesAnonymized = 0;
		int propertiesAnonymized = 0;

		try {
			// Always delete refresh tokens (security requirement)
			refreshTokensDeleted = accountDeletionService.deleteUserRefreshTokens(user);

			// Handle files and properties based on user preference
			if (deleteRequest.isDeleteAllData()) {
				log.info("WILL NOT Delete any data for user: {}", user.getEmail());
//				filesDeleted = accountDeletionService.deleteUserFiles(user);
//				propertiesDeleted = accountDeletionService.deleteUserProperties(user);
			} else if (deleteRequest.isAnonymizeData()) {
				log.info("WILL NOT Anonymize any data for user: {}", user.getEmail());
//				filesAnonymized = accountDeletionService.anonymizeUserFiles(user);
//				propertiesAnonymized = accountDeletionService.anonymizeUserProperties(user);
			}

			// Delete the user account (final step)
			userRepository.delete(user);

			// Clear security context
			SecurityContextHolder.clearContext();

			log.info("Account deletion completed for user: {} | Tokens: {} | Files: {} | Properties: {}", user.getEmail(), refreshTokensDeleted, filesDeleted, propertiesDeleted);

			return AccountDeletionResponseDto.builder()
					.message("Account deleted successfully")
					.username(user.getUsername())
					.email(user.getEmail())
					.deletedAt(DateTimeUtils.nowInIndia())
					.refreshTokensDeleted(refreshTokensDeleted)
					.filesDeleted(filesDeleted)
					.propertiesDeleted(propertiesDeleted)
					.filesAnonymized(filesAnonymized)
					.propertiesAnonymized(propertiesAnonymized)
					.build();

		} catch (Exception e) {
			log.error("Error during account deletion for user {}: {}", user.getEmail(), e.getMessage());
			throw new RuntimeException("Account deletion failed: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public UpdateProfileResponseDto updateProfile(UpdateProfileRequestDto updateRequest) {
		log.info("Profile update request received: {}", updateRequest);

		// Validate that at least one field is provided
		if (!updateRequest.hasAnyField()) {
			throw new ValidationException(ExceptionCode.N101, ExceptionCode.N101.getDefaultMessage());
		}

		// Get current authenticated user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getName() == null) {
			throw new ValidationException(ExceptionCode.A101, "User must be authenticated to update profile");
		}

		// Find the user
		User user = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> {
					log.error("User account not found for profile update: {}", auth.getName());
					return new ValidationException(ExceptionCode.A112, "User account not found");
				});

		// Check email uniqueness if email is being updated
		if (Objects.nonNull(updateRequest.getEmail()) && !updateRequest.getEmail().equalsIgnoreCase(user.getEmail())) {
			Optional<User> existingEmailUser = userRepository.findByEmail(updateRequest.getEmail());
			if (existingEmailUser.isPresent()) {
				log.error("Email already exists: {}", updateRequest.getEmail());
				throw new ResourceAlreadyExistsException(ExceptionCode.A116, ExceptionCode.A116.getDefaultMessage());
			}
		}

		// Update only provided fields
		boolean hasChanges = false;
		if (Objects.nonNull(updateRequest.getFirstName()) && !updateRequest.getFirstName().equalsIgnoreCase(user.getFirstName())) {
			user.setFirstName(updateRequest.getFirstName());
			hasChanges = true;
		}
		if (Objects.nonNull(updateRequest.getLastName()) && !updateRequest.getLastName().equalsIgnoreCase(user.getLastName())) {
			user.setLastName(updateRequest.getLastName());
			hasChanges = true;
		}
		if (Objects.nonNull(updateRequest.getEmail()) && !updateRequest.getEmail().equalsIgnoreCase(user.getEmail())) {
			user.setEmail(updateRequest.getEmail());
			hasChanges = true;
		}

		if (!hasChanges) {
			throw new ValidationException(ExceptionCode.N100, ExceptionCode.N100.getDefaultMessage());
		}

		// Save updated user
		User updatedUser = userRepository.save(user);
		log.info("Profile updated successfully for user: {}", updatedUser.getEmail());

		return UpdateProfileResponseDto.builder()
				.message("Profile updated successfully")
				.username(updatedUser.getUsername()) // Username stays the same
				.email(updatedUser.getEmail())
				.firstName(updatedUser.getFirstName())
				.lastName(updatedUser.getLastName())
				.role(updatedUser.getRole().name())
				.updatedAt(DateTimeUtils.nowInIndia())
				.build();
	}

	@Override
	@Transactional
	public ChangePasswordResponseDto changePassword(ChangePasswordRequestDto changePasswordRequest) {
		log.info("Password change request received for user");

		// Validate password confirmation
		if (!changePasswordRequest.isPasswordConfirmed()) {
			throw new ValidationException(ExceptionCode.A119, ExceptionCode.A119.getDefaultMessage());
		}

		// Get current authenticated user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getName() == null) {
			throw new ValidationException(ExceptionCode.A101, "User must be authenticated to change password");
		}

		// Find the user
		User user = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> {
					log.error("User account not found for password change: {}", auth.getName());
					return new ValidationException(ExceptionCode.A112, "User account not found");
				});

		// Verify current password
		if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
			log.error("Invalid current password provided for password change: {}", user.getEmail());
			throw new ValidationException(ExceptionCode.A117, ExceptionCode.A117.getDefaultMessage());
		}

		// Check if new password is different from current
		if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
			throw new ValidationException(ExceptionCode.A118, ExceptionCode.A118.getDefaultMessage());
		}

		try {
			// Update password
			user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
			userRepository.save(user);

			// Revoke all refresh tokens for security (force re-login on all devices)
			int revokedTokensCount = refreshTokenService.revokeAllUserTokens(user);

			// Clear current security context to force re-authentication
			SecurityContextHolder.clearContext();

			log.info("Password changed successfully for user: {}. {} refresh tokens revoked.", user.getEmail(), revokedTokensCount);

			return ChangePasswordResponseDto.builder()
					.message("Password changed successfully")
					.username(user.getUsername())
					.email(user.getEmail())
					.refreshTokensRevoked(revokedTokensCount)
					.changedAt(DateTimeUtils.nowInIndia())
					.securityNote("All sessions have been terminated. Please log in again with your new password.")
					.build();

		} catch (Exception e) {
			log.error("Error during password change for user {}: {}", user.getEmail(), e.getMessage());
			throw new RuntimeException("Password change failed: " + e.getMessage(), e);
		}
	}
}
