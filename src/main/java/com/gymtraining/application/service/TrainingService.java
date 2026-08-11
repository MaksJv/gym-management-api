package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.*;
import com.gymtraining.application.model.Training;

import java.util.List;

/**
 * Service interface for managing training sessions and assignments.
 * Acts as the primary orchestrator between trainees, trainers, and their
 * respective training types to ensure consistent scheduling and data retrieval.
 */
public interface TrainingService {

    /**
     * Creates a new training session based on the provided request.
     * Validates the existence of the trainee, trainer, and training type
     * before persisting the training entity.
     *
     * @param request the DTO containing session details (trainee ID, trainer ID, date, etc.)
     * @return the persisted {@link Training} entity
     * @throws TraineeNotFoundException if the trainee ID is invalid
     * @throws TrainerNotFoundException if the trainer ID is invalid
     * @throws TrainingTypeNotFoundException if the training type ID is invalid
     */
    Training createTrainingFromRequest(TrainingCreateRequest request);

    /**
     * Retrieves detailed information about a training session by its ID.
     * Enriches the response with full names of the participants by resolving
     * their user profiles.
     *
     * @param id the unique identifier of the training session
     * @return {@link TrainingResponse} containing session details and participant names
     * @throws RuntimeException if the training session is not found
     */
    TrainingResponse getTrainingById(Long id);

    /**
     * Creates an initial assignment between a trainee and a trainer.
     * This method initializes a default training record, effectively linking
     * the two participants and using the trainer's specialization as the default training type.
     *
     * @param traineeId the unique identifier of the trainee
     * @param trainerId the unique identifier of the trainer
     * @return {@link TrainingResponse} representing the newly created assignment
     * @throws TraineeNotFoundException if the trainee's user profile is missing
     * @throws TrainerNotFoundException if the trainer record is missing
     * @throws UserNotFoundException if the trainer's user profile is missing
     */
    TrainingResponse assignTrainee2Trainer(Long traineeId, Long trainerId);

    /**
     * Retrieves a list of trainees assigned to a specific trainer.
     * Resolves each trainee's user profile to provide their full names in the response.
     *
     * @param trainerId the unique identifier of the trainer
     * @return a {@link List} of {@link TraineeInfoResponse} containing the assigned trainees' details
     * @throws TrainerNotFoundException if the trainer record is missing
     * @throws UserNotFoundException if any trainee's user profile is missing
     */
    List<TraineeInfoResponse> fetchTraineesByTrainerId(Long trainerId);

    /**
     * Adds a new training session to an existing assignment.
     * Validates the existence of the trainee, trainer, and training type before
     * creating the new session linked to the existing assignment.
     *
     * @param request the DTO containing details for the new training session
     * @throws TraineeNotFoundException if the trainee ID is invalid
     * @throws TrainerNotFoundException if the trainer ID is invalid
     * @throws TrainingTypeNotFoundException if the training type ID is invalid
     */
    void addTraining(TrainingAdditionRequest request);

    /**
     * Retrieves a list of trainers assigned to a specific trainee.
     * Resolves each trainer's user profile to provide their full names in the response.
     *
     * @param traineeId the unique identifier of the trainee
     * @return a {@link List} of {@link TrainerInfoResponse} containing the assigned trainers' details
     * @throws TraineeNotFoundException if the trainee record is missing
     * @throws UserNotFoundException if any trainer's user profile is missing
     */
    List<TrainerInfoResponse> getTrainersByTraineeId(Long traineeId);

    /**
     * Retrieves a list of training sessions based on optional filtering criteria.
     * Supports filtering by trainee ID, trainer ID, date range, and training type.
     *
     * @param traineeId optional filter for the trainee's unique identifier
     * @param trainerId optional filter for the trainer's unique identifier
     * @return a {@link List} of {@link TrainingResponse} containing the filtered training sessions
     * @throws TraineeNotFoundException if the provided trainee ID does not exist
     * @throws TrainerNotFoundException if the provided trainer ID does not exist
     */
    List<TrainingResponse> getTrainingsByFilter(Long traineeId, Long trainerId);

    /**
     * Retrieves a specific assignment record by its ID.
     * Resolves participant names and session metadata into a unified response DTO.
     *
     * @param id the unique identifier of the assignment/training
     * @return {@link TrainingResponse} containing the assignment details
     * @throws TrainingNotFoundException if the assignment record does not exist
     */
    TrainingResponse getAssignmentById(Long id);
}
