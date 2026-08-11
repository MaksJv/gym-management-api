package com.gymtraining.application.repository;

import com.gymtraining.application.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByNameIgnoreCase(String name);

}
