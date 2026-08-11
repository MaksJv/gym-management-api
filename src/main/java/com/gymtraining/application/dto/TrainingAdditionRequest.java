package com.gymtraining.application.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainingAdditionRequest (
        @NotBlank(message = "Trainee username cannot be blank")
        @Size(min = 3, max = 50, message = "Trainee username must be between 3 and 50 characters")
        String traineeUsername,

        @NotBlank(message = "Trainer username cannot be blank")
        @Size(min = 3, max = 50, message = "Trainer username must be between 3 and 50 characters")
        String trainerUsername,

        @NotBlank(message = "Training name cannot be blank")
        @Size(min = 2, max = 100, message = "Training name must be between 2 and 100 characters")
        String trainingName,

        @NotNull(message = "Training date cannot be null")
        @FutureOrPresent(message = "Training date must be in the present or future")
        LocalDateTime trainingDate,

        @NotBlank(message = "Training type cannot be blank")
        @Size(min = 2, max = 50, message = "Training type must be between 2 and 50 characters")
        String trainingType,

        @NotNull(message = "Duration cannot be null")
        @Positive(message = "Duration must be a positive number (greater than 0)")
        Integer duration
) {
    @Override
    public String toString() {
        return "TrainingAdditionRequest{" +
                "traineeUsername=" + (traineeUsername != null ? "'" + traineeUsername + "'" : "null") +
                ", trainerUsername=" + (trainerUsername != null ? "'" + trainerUsername + "'" : "null") +
                ", trainingName=" + (trainingName != null ? "'" + trainingName + "'" : "null") +
                ", trainingDate=" + trainingDate +
                ", trainingType=" + (trainingType != null ? "'" + trainingType + "'" : "null") +
                ", duration=" + duration +
                '}';
    }
}
