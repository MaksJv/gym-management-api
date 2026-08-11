package com.gymtraining.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TraineeUpdateResponse (
        String username,
        String firstName,
        String lastName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dateOfBirth,
        String address,
        boolean active
) {

    private static final String PROTECTED_LABEL = "[PROTECTED]";

    @Override
    public String toString() {
        return "TraineeUpdateResponse{" +
                "username=" + (username != null ? "'" + username + "'" : "null") +
                ", firstName=" + (firstName != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", lastName=" + (lastName != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", dateOfBirth=" + (dateOfBirth != null ? PROTECTED_LABEL : "null") +
                ", address=" + (address != null ? "'" + PROTECTED_LABEL + "'" : "null") +
                ", active=" + active +
                '}';
    }
}
