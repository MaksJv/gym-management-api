package com.gymtraining.application.service;

import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.model.TrainingType;

import java.util.List;

/**
 * Service interface for managing training types/categories.
 * Provides functionality to maintain and retrieve the list of available
 * specializations used by trainers and training sessions.
 */
public interface TrainingTypeService {

    /**
     * Persists a new training type or updates an existing one.
     *
     * @param trainingType the entity to be saved
     * @return the persisted {@link TrainingType} entity including its generated ID
     */
    TrainingType save(TrainingType trainingType);

    /**
     * Retrieves all available training types registered in the system.
     *
     * @return a {@link List} of all {@link TrainingType} entities
     */
    List<TrainingType> getAll();

    /**
     * Finds a specific training type by its unique identifier.
     *
     * @param id the unique ID of the training type
     * @return the {@link TrainingType} entity if found
     * @throws TrainingTypeNotFoundException if no training type exists with the given ID
     */
    TrainingType getById(Long id);

    /**
     * Performs a full cascading deletion of a Training Type.
     * This operation removes the training type's primary record and all associated data,
     *
     *
     * @param id the unique ID of the training type to delete
     * @throws TrainingTypeNotFoundException if the provided ID is null
     */
    void deleteTrainingTypeById(Long id);
}
