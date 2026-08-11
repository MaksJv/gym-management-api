package com.gymtraining.application.repository;

import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUsername(String username);

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainee.id = :traineeId " +
            "AND (CAST(:fromDate AS timestamp) IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (CAST(:toDate AS timestamp) IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:trainingType IS NULL OR LOWER(t.trainingType.name) = LOWER(CAST(:trainingType AS string))) " +
            "AND (:trainerName IS NULL OR LOWER(t.trainer.username) = LOWER(CAST(:trainerName AS string)))")
    List<Training> findTrainingsByFilters(@Param("traineeId") Long traineeId,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate,
                                          @Param("trainingType") String trainingType,
                                          @Param("trainerName") String trainerName);

}
