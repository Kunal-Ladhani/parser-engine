package com.parser.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

	@NotBlank(message = "Current password is required")
	@Size(min = 1, max = 100, message = "Current password must not exceed 100 characters")
	private String currentPassword;

	@NotBlank(message = "New password is required")
	@Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
			message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
	)
	private String newPassword;

	@NotBlank(message = "Password confirmation is required")
	private String confirmPassword;

	// Helper method to validate password confirmation
	public boolean isPasswordConfirmed() {
		return Objects.nonNull(newPassword) && newPassword.equals(confirmPassword);
	}

}
