package com.parser.engine.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(exclude = {"password", "username", "email"})
public class LoginRequestDto {

	@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
	@Pattern(
			regexp = "^[a-zA-Z0-9_-]+$",
			message = "Username can only contain letters, numbers, underscores, and hyphens"
	)
	private String username;

	@Email(message = "Please provide a valid email address")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
			message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
	private String password;

	public boolean isEmailOrUsernamePresent() {
		return !StringUtils.isAllBlank(email, username);
	}

	public String getLoginIdentifier() {
		if (StringUtils.isNoneBlank(username)) {
			return username;
		}
		return email;
	}

}
