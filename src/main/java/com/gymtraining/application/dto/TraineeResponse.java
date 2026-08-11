package com.gymtraining.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TraineeResponse {
    private Long id;
    @ToString.Exclude
    private String firstName;
    @ToString.Exclude
    private String lastName;
    private String username;
    @ToString.Exclude
    private LocalDate dateOfBirth;
    @ToString.Exclude
    private String address;
    @JsonProperty("active")
    private boolean active;
}
