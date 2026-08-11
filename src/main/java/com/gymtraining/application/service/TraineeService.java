package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.EntityAlreadyExistsException;
import com.gymtraining.application.exception.TraineeNotFoundException;
import com.gymtraining.application.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining operations for Trainee management.
 * Provides high-level business logic for registration and profile retrieval,
 * coordinating between User and Trainee data domains.
 */
public interface TraineeService {

    /**
     * Creates a new trainee profile and an associated user account.
     * This method performs a uniqueness check on the username, maps the request
     * to internal entities, and persists both User and Trainee records.
     *
     * @param request the DTO containing registration details (e.g., username, personal info)
     * @return {@link TraineeResponse} containing the saved profile and generated credentials
     * @throws EntityAlreadyExistsException if a user with the requested username already exists
     */
    TraineeResponse createTraineeFromRequest(TraineeCreationRequest request);

    /**
     * Retrieves the complete profile of a trainee by their unique identifier.
     * The process involves fetching the trainee record and its corresponding
     * user account to provide a unified data view.
     *
     * @param id the unique identifier of the trainee
     * @return {@link TraineeResponse} containing the combined user and trainee data
     * @throws TraineeNotFoundException if no trainee is found with the specified ID
     * @throws UserNotFoundException if the trainee exists but the associated user account is missing
     */
    TraineeResponse getTraineeById(Long id);

    /**
    * Registers a new trainee and creates an associated user account.
    * The process involves validating username uniqueness, mapping the request
    * to internal entities, and persisting both User and Trainee records.
    *
    * @param request the DTO containing registration details (e.g., username, personal info)
    * @return {@link CredentialsResponse} containing the saved profile and generated credentials
    * @throws EntityAlreadyExistsException if a user with the requested username already exists
    */
    CredentialsResponse registerTrainee(TraineeRegistrationRequest request);

    /**
     * Retrieves the complete profile of a trainee by their username.
     * The process involves fetching the trainee record and its corresponding
     * user account to provide a unified data view.
     *
     * @param username the unique username of the trainee
     * @return {@link TraineeProfileResponse} containing the combined user and trainee data
     * @throws TraineeNotFoundException if no trainee is found with the specified username
     * @throws UserNotFoundException if the trainee exists but the associated user account is missing
     */
    TraineeProfileResponse getTraineeProfileByUsername(String username);

    /**
     * Updates the profile of an existing trainee.
     *
     * @param username the unique username of the trainee
     * @param request the DTO containing updated profile details
     * @return {@link TraineeProfileResponse} containing the updated profile data
     * @throws TraineeNotFoundException if no trainee is found with the specified username
     * @throws UserNotFoundException if the trainee exists but the associated user account is missing
     */
    TraineeUpdateResponse updateTraineeProfile(String username, TraineeUpdateRequest request);

    /**
    * Performs a full cascading deletion of a trainee account.
    * This operation removes the trainee's primary record and all associated data,
    *
    *
    * @param username the unique username of the trainee to delete
    * @throws TraineeNotFoundException if the provided username is null
    */
    void deleteTraineeByUsername(String username);


    List<TraineeTrainingResponse> getTraineeTrainings(String username,
                                                      LocalDate from,
                                                      LocalDate to,
                                                      String trainerName,
                                                      String trainingType);

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
     * Performs a full cascading deletion of a trainer account.
     * This operation removes the trainee's primary record and all associated data,
     *
     *
     * @param id the unique ID of the trainee to delete
     * @throws TraineeNotFoundException if the provided ID is null
     */
    void deleteTraineeById(Long id);
}
