package com.parser.engine.exception;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.response.ExceptionRespDto;
import com.parser.engine.utils.DateTimeUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * The type Global exception handler.
 *
 * @author Kunal Ladhani
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	// ============================== AUTHENTICATION EXCEPTIONS ==============================

	/**
	 * Handle general Spring Security AuthenticationException This catches any
	 * other authentication failures not handled above
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ExceptionRespDto> handleAuthenticationException(AuthenticationException exp, WebRequest req) {
		log.error("Authentication failed: {} from: {}", exp.getMessage(), getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A101, ExceptionCode.A101.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle Spring Security AccessDeniedException This catches when user is
	 * authenticated but doesn't have required permissions
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionRespDto> handleAccessDenied(AccessDeniedException exp, WebRequest req) {
		log.error("Access denied to {}: {} from: {}", req.getDescription(false), exp.getMessage(), getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A102, ExceptionCode.A102.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	// ============================== JWT EXCEPTIONS ==============================

	/**
	 * Handle JWT token expiration
	 */
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ExceptionRespDto> handleExpiredJwtException(ExpiredJwtException exp, WebRequest req) {
		log.error("Expired JWT token from: {}", getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A103, ExceptionCode.A103.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle malformed JWT tokens
	 */
	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<ExceptionRespDto> handleMalformedJwtException(MalformedJwtException exp, WebRequest req) {
		log.error("Malformed JWT token from: {}", getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A104, ExceptionCode.A104.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle JWT signature validation failures
	 */
	@ExceptionHandler(SecurityException.class)
	public ResponseEntity<ExceptionRespDto> handleJwtSecurityException(SecurityException exp, WebRequest req) {
		log.error("JWT signature validation failed from: {}", getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A105, ExceptionCode.A105.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle unsupported JWT tokens
	 */
	@ExceptionHandler(UnsupportedJwtException.class)
	public ResponseEntity<ExceptionRespDto> handleUnsupportedJwtException(UnsupportedJwtException exp, WebRequest req) {
		log.error("Unsupported JWT token from: {}", getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A106, ExceptionCode.A106.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle general JWT exceptions
	 */
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ExceptionRespDto> handleJwtException(JwtException exp, WebRequest req) {
		log.error("JWT processing error: {} from: {}", exp.getMessage(), getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A107, ExceptionCode.A107.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	// ============================== AUTHENTICATION EXCEPTIONS ==============================

	/**
	 * Handle account status exceptions (disabled, locked, expired)
	 */
	@ExceptionHandler({DisabledException.class, LockedException.class, AccountExpiredException.class})
	public ResponseEntity<ExceptionRespDto> handleAccountStatusException(AuthenticationException exp, WebRequest req) {
		log.error("Account status issue: {} from: {}", exp.getMessage(), getClientInfo(req));

		ExceptionCode code;
		String message;

		if (exp instanceof DisabledException) {
			code = ExceptionCode.A108;
			message = ExceptionCode.A108.getDefaultMessage();
		} else if (exp instanceof LockedException) {
			code = ExceptionCode.A109;
			message = ExceptionCode.A109.getDefaultMessage();
		} else {
			code = ExceptionCode.A110;
			message = ExceptionCode.A110.getDefaultMessage();
		}

		ExceptionRespDto response = createErrorResponse(code, message);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle Spring Security BadCredentialsException This catches wrong
	 * username/password combinations
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ExceptionRespDto> handleBadCredentials(BadCredentialsException exp, WebRequest req) {
		log.error("Bad credentials Exception occurred: {}", exp.getMessage(), exp);
		log.error("Bad credentials attempted from: {}", getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A111, ExceptionCode.A111.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handle Spring Security UsernameNotFoundException This catches when user
	 * doesn't exist in database
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ExceptionRespDto> handleUsernameNotFound(UsernameNotFoundException exp, WebRequest req) {
		log.error("Username not found: {} from: {}", exp.getMessage(), getClientInfo(req));
		ExceptionRespDto response = createErrorResponse(ExceptionCode.A112, ExceptionCode.A112.getDefaultMessage());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	// ============================== EXISTING HANDLERS ==============================

	/**
	 * General exception handler response entity.
	 *
	 * @param exp the exception
	 * @param req the web request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionRespDto> generalExceptionHandler(Exception exp, WebRequest req) {
		log.error("General Exception occurred: {}", exp.getMessage(), exp);
		ExceptionRespDto ExceptionRespDto = new ExceptionRespDto();
		ExceptionRespDto.setTimestamp(DateTimeUtils.nowInIndia());
		ExceptionRespDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionRespDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Method argument not valid exception handler response entity.
	 *
	 * @param exp the exception
	 * @return the response entity
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionRespDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exp, WebRequest req) {
		log.error("MethodArgumentNotValidException occurred: {}", exp.getMessage(), exp);
		var allErrors = exp.getBindingResult()
				.getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
		ExceptionRespDto ExceptionRespDto = new ExceptionRespDto();
		ExceptionRespDto.setTimestamp(DateTimeUtils.nowInIndia());
		ExceptionRespDto.setExceptionCode(ExceptionCode.V101);
		ExceptionRespDto.setErrorMessage(allErrors.toString());
		return new ResponseEntity<>(ExceptionRespDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({InvalidFileContentException.class, InvalidFileTypeException.class})
	public ResponseEntity<ExceptionRespDto> invalidFileExceptionHandler(ServiceException exp, WebRequest req) {
		log.error("Exception occurred: {}", exp.getMessage(), exp);
		ExceptionRespDto ExceptionRespDto = new ExceptionRespDto();
		ExceptionRespDto.setTimestamp(DateTimeUtils.nowInIndia());
		ExceptionRespDto.setExceptionCode(exp.getExceptionCode());
		ExceptionRespDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionRespDto, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Resource does not exists exception handler response entity.
	 *
	 * @param exp the exception
	 * @return the response entity
	 */
	@ExceptionHandler(ResourceDoesNotExistsException.class)
	public ResponseEntity<ExceptionRespDto> resourceDoesNotExistsExceptionHandler(ResourceDoesNotExistsException exp, WebRequest req) {
		log.error("ResourceDoesNotExistsException occurred: {}", exp.getMessage(), exp);
		ExceptionRespDto ExceptionRespDto = new ExceptionRespDto();
		ExceptionRespDto.setTimestamp(DateTimeUtils.nowInIndia());
		ExceptionRespDto.setExceptionCode(exp.getExceptionCode());
		ExceptionRespDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionRespDto, HttpStatus.NOT_FOUND);
	}

	/**
	 * service exception handler response entity.
	 *
	 * @param exp the exception
	 * @return the response entity
	 */
	@ExceptionHandler({ServiceException.class,
			ResourceAlreadyExistsException.class,
			OperationNotAllowedException.class
	})
	public ResponseEntity<ExceptionRespDto> serviceExceptionHandler(ServiceException exp, WebRequest req) {
		log.error("Exception occurred: {}", exp.getMessage(), exp);
		ExceptionRespDto ExceptionRespDto = new ExceptionRespDto();
		ExceptionRespDto.setTimestamp(DateTimeUtils.nowInIndia());
		ExceptionRespDto.setExceptionCode(exp.getExceptionCode());
		ExceptionRespDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionRespDto, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle custom ValidationException (for your custom auth logic)
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ExceptionRespDto> validationExceptionHandler(ValidationException exp, WebRequest req) {
		log.error("ValidationException occurred: {} from: {}", exp.getMessage(), getClientInfo(req));

		// Determine HTTP status based on exception code
		HttpStatus status = getHttpStatusForValidationException(exp.getExceptionCode());

		ExceptionRespDto response = new ExceptionRespDto();
		response.setTimestamp(DateTimeUtils.nowInIndia());
		response.setExceptionCode(exp.getExceptionCode());
		response.setErrorMessage(exp.getMessage());

		return new ResponseEntity<>(response, status);
	}

	// ============================== HELPER METHODS ==============================

	/**
	 * Helper method to create standardized error responses
	 */
	private ExceptionRespDto createErrorResponse(ExceptionCode code, String message) {
		ExceptionRespDto response = new ExceptionRespDto();
		response.setTimestamp(DateTimeUtils.nowInIndia());
		response.setExceptionCode(code);
		response.setErrorMessage(message);
		return response;
	}

	/**
	 * Helper method to extract client information from request
	 */
	private String getClientInfo(WebRequest request) {
		String clientIp = request.getHeader("X-Forwarded-For");
		if (clientIp == null || clientIp.isEmpty()) {
			clientIp = request.getHeader("X-Real-IP");
		}
		if (clientIp == null || clientIp.isEmpty()) {
			clientIp = "unknown";
		}

		String userAgent = request.getHeader("User-Agent");
		return String.format("IP: %s, User-Agent: %s",
				clientIp,
				userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 50)) : "unknown"
		);
	}

	/**
	 * Helper method to determine HTTP status based on ValidationException code
	 */
	private HttpStatus getHttpStatusForValidationException(ExceptionCode code) {
		return switch (code.getCode()) {
			// Authentication failures should return 401 UNAUTHORIZED
			case "A101", // Authentication failed
				 "A103", "A104", "A105", "A106", "A107", // JWT token errors
				 "A108", "A109", "A110", // Account status (disabled, locked, expired)
				 "A111", "A112", "A113", // Login failures (bad credentials, not found, wrong password)
				 "A121", "A122" // Refresh token security errors
					-> HttpStatus.UNAUTHORIZED;

			// Access denied should return 403 FORBIDDEN
			case "A102" -> HttpStatus.FORBIDDEN;

			// User registration conflicts and file duplicates should return 409 CONFLICT
			case "A115", // Username already registered
				 "A116", // Email already registered
				 "F110" // File with this name already exists
					-> HttpStatus.CONFLICT;

			// Form validation errors should return 400 BAD_REQUEST (user stays on current page)
			case "A117", // Current password incorrect (during password change - don't logout user!)
				 "A118", // New password must be different from current
				 "A119", // Password confirmation does not match
				 "A120", // Username format invalid
				 "N101", // At least one field must be provided for update
				 "N102" // No valid fields provided for update
					-> HttpStatus.BAD_REQUEST;

			// Permission-related codes should return 403 FORBIDDEN
			case "O101" -> HttpStatus.FORBIDDEN;

			// File not found should return 404 NOT_FOUND
			case "F106", "P102" -> HttpStatus.NOT_FOUND;

			// Validation errors should return 400 BAD_REQUEST (default)
			default -> HttpStatus.BAD_REQUEST;
		};
	}

}
