package com.gymtraining.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TraineeUpdateRequest(
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotNull(message = "Date of birth cannot be null")
        @Past(message = "Date of birth must be in the past")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @NotNull(message = "Active status must be specified")
        Boolean active
) {

    private static final String PROTECTED_LABEL = "[PROTECTED]";

    @Override
    public String toString() {
        return "TraineeUpdateRequest{" +
                "firstName=" + (firstName != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", lastName=" + (lastName != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", dateOfBirth=" + (dateOfBirth != null ? PROTECTED_LABEL : "null") +
                ", address=" + (address != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", active=" + active +
                '}';
    }
}
