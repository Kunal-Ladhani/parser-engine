package com.parser.engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponseDto {

	private String message;
	private String username;
	private String email;
	private int refreshTokensRevoked;
	private LocalDateTime changedAt;
	private String securityNote;

}
