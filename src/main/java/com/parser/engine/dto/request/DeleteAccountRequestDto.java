package com.parser.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DeleteAccountRequestDto {

    @NotBlank(message = "Password confirmation is required")
    private String currentPassword;

    @NotBlank(message = "Confirmation text is required")
    @Pattern(
            regexp = "DELETE MY ACCOUNT",
            message = "You must type exactly 'DELETE MY ACCOUNT' to confirm"
    )
    private String confirmationText;

    private boolean deleteAllData = true; // Delete files and properties
    private boolean anonymizeData = false; // Keep data but remove user references
}
