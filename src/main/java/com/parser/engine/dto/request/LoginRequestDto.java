package com.parser.engine.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(exclude = {"password"})
public class LoginRequestDto {

	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String username;

	@Email(message = "Valid email is required")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = " Password must be at least 6 characters")
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
