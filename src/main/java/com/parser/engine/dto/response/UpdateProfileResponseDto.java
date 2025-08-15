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
public class UpdateProfileResponseDto {

	private String message;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String role;
	private LocalDateTime updatedAt;

}
