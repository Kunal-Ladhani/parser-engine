package com.parser.engine.controller;

import com.parser.engine.dto.request.*;
import com.parser.engine.dto.response.*;
import com.parser.engine.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	@Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/v1/signup")
	public ResponseEntity<TokenResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
		log.info("Signup attempt with SignupRequestDto: {} ", signupRequest);
		TokenResponseDto response = authService.signup(signupRequest);
		log.info("User {} signed up successfully", response.getUsername());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/v1/login")
	public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
		log.info("Login attempt with LoginRequestDto: {}", loginRequest);
		TokenResponseDto response = authService.login(loginRequest);
		log.info("User: {} logged in successfully", response.getUsername());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/v1/refresh")
	public ResponseEntity<TokenResponseDto> refreshToken(
			@RequestHeader("X-Refresh-Token") String refreshToken,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		log.info("Refresh token request received.");
		TokenResponseDto response = authService.refreshToken(refreshToken, authorizationHeader);
		log.info("Refresh token successful for user: {}", response.getUsername());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/v1/logout")
	public ResponseEntity<LogoutResponseDto> logout() {
		log.info("Logout request received.");
		LogoutResponseDto response = authService.logout();
		log.info("Logout completed for user: {}", response.getUsername());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/v1/delete-account")
	public ResponseEntity<AccountDeletionResponseDto> deleteAccount(@Valid @RequestBody DeleteAccountRequestDto deleteRequest) {
		log.info("Account deletion request received");
		AccountDeletionResponseDto response = authService.deleteAccount(deleteRequest);
		log.info("Account deletion completed for user: {}", response.getUsername());
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/v1/update-profile")
	public ResponseEntity<UpdateProfileResponseDto> updateProfile(@Valid @RequestBody UpdateProfileRequestDto updateRequest) {
		log.info("Profile update request received: {}", updateRequest);
		UpdateProfileResponseDto response = authService.updateProfile(updateRequest);
		log.info("Profile updated successfully for user: {}", response.getUsername());
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/v1/change-password")
	public ResponseEntity<ChangePasswordResponseDto> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequest) {
		log.info("Password change request received");
		ChangePasswordResponseDto response = authService.changePassword(changePasswordRequest);
		log.info("Password changed successfully for user: {}", response.getUsername());
		return ResponseEntity.ok(response);
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty()) {
			return xRealIp;
		}

		return request.getRemoteAddr();
	}
}
