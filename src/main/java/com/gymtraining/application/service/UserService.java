package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.UserNotFoundException;
import com.gymtraining.application.model.User;

import java.util.List;

/**
 * Service interface for managing core User account operations.
 * Handles authentication details, profile updates, and cascading account deletions
 * across the trainee and trainer domains.
 */
public interface UserService {

    /**
     * Retrieves a user's account details by their unique identifier.
     *
     * @param id the unique ID of the user
     * @return {@link UserResponse} containing the user's profile data
     * @throws UserNotFoundException if no user is found with the given ID
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves a list of all currently active users in the system.
     *
     * @return a {@link List} of {@link UserResponse} for all active accounts
     */
    List<UserResponse> getAllUsers();

    /**
     * Updates an existing user's profile information.
     * Allows modification of names and credentials. If a new password is provided,
     * it updates the existing one.
     *
     * @param id          the unique ID of the user to update
     * @param updatedUser the DTO containing the new profile data
     * @return {@link UserResponse} representing the updated state of the user
     * @throws UserNotFoundException if the user to be updated does not exist
     */
    UserResponse updateUser(Long id, UserUpdateRequest updatedUser);

    /**
     * Authenticates a user based on the provided login credentials.
     * Validates the username and password against stored records and returns
     * an authentication token or session information upon successful authentication.
     *
     * @param request the DTO containing login credentials (username and password)
     * @throws UserNotFoundException if the username does not exist or the password is incorrect
     */
    void authenticate(LoginRequest request);

    /**
     * Retrieves a user's account details by their unique username.
     *
     * @param username the unique username of the user
     * @return {@link UserResponse} containing the user's profile data
     * @throws UserNotFoundException if no user is found with the given username
     */
    User getByUsername(String username);

    /**
     * Performs a full cascading deletion of a user account.
     * This operation removes the user's primary record and all associated data,
     * including trainee/trainer profiles and their linked training sessions.
     *
     * @param id the unique ID of the user to delete
     * @throws UserNotFoundException if the provided ID is null
     */
    void deleteUserById(Long id);
}
