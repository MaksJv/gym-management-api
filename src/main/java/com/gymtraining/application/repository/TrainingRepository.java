package com.gymtraining.application.repository;

import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findAllByTraineeId(Long traineeId);

    List<Training> findAllByTrainerId(Long trainerId);

    boolean existsByTrainingTypeId(Long trainingTypeId);

    void deleteByTraineeId(Long traineeId);

    @Query("SELECT DISTINCT t.trainee FROM Training t " +
            "WHERE t.trainer.id = :trainerId " +
            "AND t.trainee.active = true")
    List<Trainee> findActiveTraineesByTrainerId(@Param("trainerId") Long trainerId);

    @Query("SELECT DISTINCT t.trainer FROM Training t " +
            "JOIN FETCH t.trainer.specialization " +
            "WHERE t.trainee.id = :traineeId " +
            "AND t.trainer.active = true")
    List<Trainer> findActiveTrainersByTraineeId(@Param("traineeId") Long traineeId);

    @Query("SELECT t FROM Training t " +
            "JOIN FETCH t.trainee " +
            "JOIN FETCH t.trainer " +
            "WHERE (:traineeId IS NULL OR t.trainee.id = :traineeId) " +
            "AND (:trainerId IS NULL OR t.trainer.id = :trainerId)")
    List<Training> findTrainingsByTraineeAndTrainer(@Param("traineeId") Long traineeId,
                                                    @Param("trainerId") Long trainerId);

}
