package com.gymtraining.application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_types")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
}
