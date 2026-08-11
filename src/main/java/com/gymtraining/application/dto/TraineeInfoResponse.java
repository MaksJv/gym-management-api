package com.gymtraining.application.dto;

import lombok.Builder;

@Builder
public record TraineeInfoResponse (
        String username,
        String firstName,
        String lastName
) {
    @Override
    public String toString() {
        return "TraineeInfoResponse{" +
                "username='" + (username != null ? "[PROTECTED_USER]" : "null") + '\'' +
                ", firstName='[PROTECTED]'" +
                ", lastName='[PROTECTED]'" +
                '}';
    }
}
