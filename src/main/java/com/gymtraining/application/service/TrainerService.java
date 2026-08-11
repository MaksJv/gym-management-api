package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.TrainerNotFoundException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining operations for Trainer management.
 * Coordinates the business logic across User, Trainer, and Specialization (TrainingType) domains.
 */
public interface TrainerService {

    /**
     * Registers a new trainer and creates an associated user account.
     * The process involves validating username uniqueness, verifying the existence
     * of the selected specialization, and persisting both User and Trainer records.
     *
     * @param request the DTO containing registration details and specialization ID
     * @return {@link TrainerResponse} containing the saved trainer profile and user account details
     * @throws IllegalArgumentException      if the provided username is already in use
     * @throws TrainingTypeNotFoundException if the provided specialization ID does not exist
     */
    TrainerResponse createTrainerFromRequest(TrainerCreationRequest request);

    /**
     * Retrieves a trainer's complete profile by their unique identifier.
     * This method aggregates data from the trainer record and the associated
     * user account to provide a comprehensive profile view.
     *
     * @param id the unique identifier of the trainer
     * @return {@link TrainerResponse} containing the combined profile and account info
     * @throws TrainerNotFoundException if no trainer is found with the specified ID
     * @throws UserNotFoundException    if the trainer exists but the associated user account is missing
     */
    TrainerResponse getTrainerById(Long id);

    TrainerProfileResponse getTrainerProfileByUsername(String username);

    /**
    * Registers a new trainer and creates an associated user account.
    * The process involves validating username uniqueness, verifying the existence
    * of the selected specialization, and persisting both User and Trainer records.
    *
    * @param request the DTO containing registration details and specialization ID
    * @return {@link CredentialsResponse} containing the saved trainer profile and generated credentials
    * @throws IllegalArgumentException      if the provided username is already in use
    * @throws TrainingTypeNotFoundException if the provided specialization ID does not exist
    */
    CredentialsResponse registerTrainer(TrainerRegistrationRequest request);

    /**
     * Updates an existing trainer's profile information.
     * Allows modification of personal details and specialization. If a new password is provided,
     * it updates the existing one in the associated user account.
     *
     * @param username the unique username of the trainer to update
     * @param request  the DTO containing the new profile data
     * @return {@link TrainerUpdateResponse} representing the updated state of the trainer
     * @throws TrainerNotFoundException      if no trainer is found with the specified username
     * @throws UserNotFoundException         if the trainer exists but the associated user account is missing
     * @throws TrainingTypeNotFoundException if the provided specialization ID does not exist
     */
    TrainerUpdateResponse updateTrainerProfile(String username, TrainerUpdateRequest request);

    /**
     * Retrieves a list of training sessions assigned to a specific trainer.
     * Supports optional filtering by date range and trainee name.
     *
     * @param username the unique username of the trainer
     * @param from     optional start date for filtering sessions
     * @param to       optional end date for filtering sessions
     * @param traineeName optional filter for trainee's name (partial match)
     * @return a {@link List} of {@link TrainerTrainingResponse} containing the filtered training sessions
     * @throws TrainerNotFoundException if the trainer record is missing
     * @throws UserNotFoundException if the trainer's user profile is missing
     */
    List<TrainerTrainingResponse> getTrainerTrainings (String username,
                                                       LocalDate from,
                                                       LocalDate to,
                                                       String traineeName);

    /**
     * Retrieves a list of trainees assigned to a specific trainer.
     * Resolves each trainee's user profile to provide their full names in the response.
     *
     * @param trainerId the unique identifier of the trainer
     * @return a {@link List} of {@link TraineeInfoResponse} containing the assigned trainees' details
     * @throws TrainerNotFoundException if the trainer record is missing
     * @throws UserNotFoundException if any trainee's user profile is missing
     */
    List<TraineeInfoResponse> getTraineesByTrainerId(Long trainerId);

    /**
     * Performs a full cascading deletion of a trainer account.
     * This operation removes the trainer's primary record and all associated data,
     *
     *
     * @param id the unique ID of the trainer to delete
     * @throws TrainerNotFoundException if the provided ID is null
     */
    void deleteTrainerById(Long id);
}
