package com.gymtraining.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TraineeRegistrationRequest(
        @NotBlank(message = "First name must not be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[A-Za-zА-Яа-яІіЇїЄє'\\-\\s]+$", message = "First name contains invalid characters")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[A-Za-zА-Яа-яІіЇїЄє'\\-\\s]+$", message = "Last name contains invalid characters")
        String lastName,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 255, message = "Address is too long (max 255 characters)")
        String address
) {

    private static final String PROTECTED_LABEL = "[PROTECTED]";

    @Override
    public String toString() {
        return "TraineeRegistrationRequest{" +
                "firstName=" + (firstName != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", lastName=" + (lastName != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", dateOfBirth=" + (dateOfBirth != null ? PROTECTED_LABEL : "null") +
                ", address=" + (address != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                '}';
    }
}
