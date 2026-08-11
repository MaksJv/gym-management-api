package com.gymtraining.application.dto;

import java.util.List;

public record TrainerProfileResponse (
        String firstName,
        String lastName,
        String specialization,
        boolean active,
        List<TraineeInfoResponse> trainees
) {
    @Override
    public String toString() {
        return "TrainerProfileResponse{" +
                "firstName=" + (firstName != null ? "'[PROTECTED]'" : "null") +
                ", lastName=" + (lastName != null ? "'[PROTECTED]'" : "null") +
                ", specialization=" + (specialization != null ? "'" + specialization + "'" : "null") +
                ", active=" + active +
                ", traineesCount=" + (trainees != null ? trainees.size() : 0) +
                '}';
    }
}
