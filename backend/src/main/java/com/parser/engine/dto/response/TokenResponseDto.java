package com.parser.engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {

	private String accessToken;
	private String refreshToken;
	private String tokenType;
	private long expiresIn; // Access token expiration in seconds
	private String username;
	private String email;
	private String role;
	private String firstName;
	private String lastName;
}
