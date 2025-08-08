package com.parser.engine.dto.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeletionResponseDto {

	private String message;
	private String username;
	private String email;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Kolkata")
	private ZonedDateTime deletedAt;

	// Summary of deleted data
	private int refreshTokensDeleted;
	private int filesDeleted;
	private int propertiesDeleted;

	// Or anonymized data
	private int filesAnonymized;
	private int propertiesAnonymized;
}
