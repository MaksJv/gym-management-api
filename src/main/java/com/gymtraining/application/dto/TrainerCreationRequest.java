package com.gymtraining.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrainerCreationRequest {
    @ToString.Exclude
    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "First name can only contain letters and standard separators")
    private String firstName;

    @ToString.Exclude
    @NotBlank(message = "Last name is mandatory")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Last name can only contain letters and standard separators")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 32, message = "Username must be between 4 and 32 characters")
    @Pattern(regexp = "^[a-z0-9._-]+$", message = "Username must be lowercase and can contain dots, underscores, or hyphens")
    private String username;

    @ToString.Exclude
    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must include uppercase, lowercase, number, and special character"
    )
    private String password;

    @NotNull(message = "Specialization ID is required")
    @Positive(message = "Specialization ID must be a valid positive number")
    private Long specializationId;
}
