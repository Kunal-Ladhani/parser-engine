package com.parser.engine.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@ToString(exclude = "email")
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProfileRequestDto {

	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String firstName;

	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	private String lastName;

	@Email(message = "Please provide a valid email address")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;

	// Username removed - usernames should be immutable for security and audit trail
	// Helper method to check if any field is provided
	public boolean hasAnyField() {
		return Objects.nonNull(firstName) || Objects.nonNull(lastName) || Objects.nonNull(email);
	}

}
