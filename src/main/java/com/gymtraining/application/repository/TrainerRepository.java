package com.gymtraining.application.repository;

import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUsername(String username);

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainer.id = :trainerId " +
            "AND (CAST(:fromDate AS timestamp) IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (CAST(:toDate AS timestamp) IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:traineeName IS NULL OR LOWER(t.trainee.username) = LOWER(CAST(:traineeName AS string)))")
    List<Training> findTrainerTrainingsByFilters(@Param("trainerId") Long trainerId,
                                                 @Param("fromDate") LocalDateTime fromDate,
                                                 @Param("toDate") LocalDateTime toDate,
                                                 @Param("traineeName") String traineeName);

    boolean existsBySpecializationId(Long specializationId);

}
