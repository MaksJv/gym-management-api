package com.gymtraining.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrainingResponse {
    private Long id;
    private String trainingName;
    private LocalDateTime trainingDate;
    private Integer duration;
    private String trainingTypeName;
    private String traineeName;
    private String trainerName;
}
