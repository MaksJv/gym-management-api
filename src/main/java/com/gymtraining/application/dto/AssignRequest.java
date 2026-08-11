package com.gymtraining.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AssignRequest {

    @NotNull(message = "Trainee ID cannot be null")
    @Positive(message = "Trainee ID must be a positive number")
    private Long traineeId;

    @NotNull(message = "Trainer ID cannot be null")
    @Positive(message = "Trainer ID must be a positive number")
    private Long trainerId;
}
