package com.gymtraining.application.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "trainees")
@PrimaryKeyJoinColumn(name = "user_id")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Trainee extends User {

    @Column(name = "date_of_birth", columnDefinition = "DATE")
    @ToString.Exclude
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 255)
    @ToString.Exclude
    private String address;
}
