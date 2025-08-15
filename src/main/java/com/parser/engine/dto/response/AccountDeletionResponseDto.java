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
public class AccountDeletionResponseDto {

	private String message;
	private String username;
	private String email;
	private LocalDateTime deletedAt;

	// Summary of deleted data
	private int refreshTokensDeleted;
	private int filesDeleted;
	private int propertiesDeleted;

	// Or anonymized data
	private int filesAnonymized;
	private int propertiesAnonymized;
}
