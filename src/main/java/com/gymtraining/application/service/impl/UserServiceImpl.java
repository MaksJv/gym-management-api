package com.gymtraining.application.service.impl;

import com.gymtraining.application.dto.LoginRequest;
import com.gymtraining.application.dto.UserResponse;
import com.gymtraining.application.dto.UserUpdateRequest;
import com.gymtraining.application.exception.AuthenticationException;
import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.UserNotFoundException;
import com.gymtraining.application.mapper.UserMapper;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.User;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User not found with id: %d";

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND, id))
        );
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByActiveTrue().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest updatedUser) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND, id))
        );

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public void authenticate(LoginRequest request) {
        User user = userRepository.findByUsernameAndActiveTrue(request.username())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!user.getPassword().equals(request.password())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("User account is inactive.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        return userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND, id))
        );

        boolean hasFutureTrainings = false;

        if (user instanceof Trainee) {
            hasFutureTrainings = trainingRepository.findAllByTraineeId(id).stream()
                    .anyMatch(t -> t.getTrainingDate().isAfter(LocalDateTime.now()));
        } else if (user instanceof Trainer) {
            hasFutureTrainings = trainingRepository.findAllByTrainerId(id).stream()
                    .anyMatch(t -> t.getTrainingDate().isAfter(LocalDateTime.now()));
        }

        if (hasFutureTrainings) {
            throw new DataIntegrityViolationException("Cannot delete user with id %d because they have future trainings".formatted(id));
        }

        user.setActive(false);
        userRepository.save(user);
    }
}
