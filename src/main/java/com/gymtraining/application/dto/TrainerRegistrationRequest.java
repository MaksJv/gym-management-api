package com.gymtraining.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TrainerRegistrationRequest(
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotNull(message = "Specialization ID cannot be null")
        @Positive(message = "Specialization ID must be a positive number")
        Long specializationId
) {
    @Override
    public @NotNull String toString() {
        return "TrainerRegistrationRequest{" +
                "firstName=" + (firstName != null ? "'[PROTECTED]'" : "null") +
                ", lastName=" + (lastName != null ? "'[PROTECTED]'" : "null") +
                ", specializationId=" + specializationId +
                '}';
    }
}
