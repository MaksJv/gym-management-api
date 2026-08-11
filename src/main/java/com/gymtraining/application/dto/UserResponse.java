package com.gymtraining.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {
    private Long id;
    @ToString.Exclude
    private String firstName;
    @ToString.Exclude
    private String lastName;
    private String username;
    @JsonProperty("active")
    private boolean active;
}
