package com.gymtraining.application.dto;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileResponse(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address,
        boolean active,
        List<TrainerInfoResponse> trainers
) {
    @Override
    public String toString() {
        return "TraineeProfileResponse{" +
                "firstName='[PROTECTED]'" +
                ", lastName='[PROTECTED]'" +
                ", dateOfBirth=[PROTECTED]" +
                ", address='[PROTECTED]'" +
                ", active=" + active +
                ", trainersCount=" + (trainers != null ? trainers.size() : 0) +
                '}';
    }
}
