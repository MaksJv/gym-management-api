package com.gymtraining.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrainingCreateRequest {
    @NotNull(message = "Trainee ID is required")
    @Positive(message = "Trainee ID must be a valid positive number")
    private Long traineeId;

    @NotNull(message = "Trainer ID is required")
    @Positive(message = "Trainer ID must be a valid positive number")
    private Long trainerId;

    @NotBlank(message = "Training name is mandatory")
    @Size(min = 3, max = 100, message = "Training name must be between 3 and 100 characters")
    @Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "Training name contains unsupported characters")
    private String trainingName;

    @NotNull(message = "Training type ID is required")
    @Positive(message = "Training type ID must be a valid positive number")
    private Long trainingTypeId;

    @NotNull(message = "Training date is required")
    @FutureOrPresent(message = "Training date must not be in the past")
    private LocalDateTime trainingDate;

    @Positive(message = "Duration must be a positive number")
    @Min(value = 10, message = "Minimum training duration is 10 minutes")
    @Max(value = 480, message = "Training duration cannot exceed 8 hours")
    private int durationMinutes;
}
