package com.gymtraining.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TrainerUpdateRequest (
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotNull(message = "Active status must be specified")
        @JsonProperty("active")
        Boolean isActive
) {
    @Override
    public String toString() {
        return "TrainerUpdateRequest{" +
                "firstName=" + (firstName != null ? "'[PROTECTED]'" : "null") +
                ", lastName=" + (lastName != null ? "'[PROTECTED]'" : "null") +
                ", active=" + isActive +
                '}';
    }
}
