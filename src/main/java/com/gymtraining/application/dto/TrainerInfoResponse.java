package com.gymtraining.application.dto;

import lombok.Builder;

@Builder
public record TrainerInfoResponse (
        String username,
        String firstName,
        String lastName,
        String specialization
) {
    @Override
    public String toString() {
        return "TrainerInfoResponse{" +
                "username=" + (username != null ? "'" + username + "'" : "null") +
                ", firstName=" + (firstName != null ? "'[PROTECTED]'" : "null") +
                ", lastName=" + (lastName != null ? "'[PROTECTED]'" : "null") +
                ", specialization=" + (specialization != null ? "'" + specialization + "'" : "null") +
                '}';
    }
}
