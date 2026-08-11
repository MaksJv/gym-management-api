package com.gymtraining.application.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainerTrainingResponse(
        String trainingName,
        LocalDateTime trainingDate,
        String trainingType,
        Integer duration,
        String traineeName
) {}
