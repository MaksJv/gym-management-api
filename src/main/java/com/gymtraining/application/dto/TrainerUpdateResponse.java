package com.gymtraining.application.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TrainerUpdateResponse (
        String username,
        String firstName,
        String lastName,
        String specialization,
        boolean active,
        List<TraineeInfoResponse> trainees
) {
    @Override
    public String toString() {
        return "TrainerUpdateResponse{" +
                "username=" + (username != null ? "'" + username + "'" : "null") +
                ", firstName=" + (firstName != null ? "'[PROTECTED]'" : "null") +
                ", lastName=" + (lastName != null ? "'[PROTECTED]'" : "null") +
                ", specialization=" + (specialization != null ? "'" + specialization + "'" : "null") +
                ", active=" + active +
                ", traineesCount=" + (trainees != null ? trainees.size() : 0) +
                '}';
    }
}
