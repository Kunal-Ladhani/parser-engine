package com.parser.engine.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteAccountRequestDto {

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
			message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
	private String currentPassword;

	@NotBlank(message = "Confirmation text is required")
	@Pattern(
			regexp = "DELETE MY ACCOUNT",
			message = "You must type exactly 'DELETE MY ACCOUNT' to confirm"
	)
	private String confirmationText;

	@Builder.Default
	private boolean deleteAllData = false; // Delete files and properties

	@Builder.Default
	private boolean anonymizeData = false; // Keep data but remove user references
}
