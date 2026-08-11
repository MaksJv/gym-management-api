package com.gymtraining.application.service;

import com.gymtraining.application.dto.LoginRequest;
import com.gymtraining.application.dto.UserResponse;
import com.gymtraining.application.dto.UserUpdateRequest;
import com.gymtraining.application.exception.AuthenticationException;
import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.UserNotFoundException;
import com.gymtraining.application.mapper.UserMapper;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.User;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserByIdShouldReturnUserResponseDTOWhenIdExists() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setUsername("test_user");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(id);
        expectedResponse.setUsername("test_user");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.getUserById(id);

        assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    void getUserByIdShouldThrowUserNotFoundExceptionWhenIdDoesNotExist() {
        Long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 999");

        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsersShouldReturnListOfUserResponseDTOWhenUsersExists() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        UserResponse dto1 = new UserResponse();
        dto1.setId(1L);
        UserResponse dto2 = new UserResponse();
        dto2.setId(2L);

        when(userRepository.findAllByActiveTrue()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponseDto(user1)).thenReturn(dto1);
        when(userMapper.toResponseDto(user2)).thenReturn(dto2);

        List<UserResponse> result = userService.getAllUsers();

        assertAll(
                () -> assertThat(result).hasSize(2).containsExactly(dto1, dto2),
                () -> verify(userMapper, times(2)).toResponseDto(any(User.class))
        );
    }

    @Test
    void getAllUsersShouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAllByActiveTrue()).thenReturn(List.of());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserShouldReturnUserResponseDTOWhenUpdateIsSuccessful() {
        Long id = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("NewName");
        updateRequest.setLastName("NewLastName");
        updateRequest.setPassword("newPass123");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setFirstName("OldName");

        User savedUser = new User();
        savedUser.setId(id);
        savedUser.setFirstName("NewName");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.updateUser(id, updateRequest);

        assertAll(
                () -> assertThat(actualResponse).isEqualTo(expectedResponse),
                () -> assertThat(existingUser.getFirstName()).isEqualTo("NewName"),
                () -> assertThat(existingUser.getLastName()).isEqualTo("NewLastName"),
                () -> assertThat(existingUser.getPassword()).isEqualTo("newPass123"),
                () -> verify(userRepository).save(existingUser)
        );
    }

    @Test
    void updateUserShouldUpdatePasswordWhenPasswordIsProvided() {
        Long id = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("secret123");

        User existingUser = new User();
        existingUser.setPassword("oldPass");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userMapper.toResponseDto(any())).thenReturn(new UserResponse());

        userService.updateUser(id, request);

        assertThat(existingUser.getPassword()).isEqualTo("secret123");
    }

    @Test
    void updateUserShouldNotUpdatePasswordWhenPasswordIsNull() {
        Long id = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword(null);

        User existingUser = new User();
        existingUser.setPassword("oldPass");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toResponseDto(any())).thenReturn(new UserResponse());

        userService.updateUser(id, request);

        assertThat(existingUser.getPassword()).isEqualTo("oldPass");
    }

    @Test
    void updateUserShouldNotUpdatePasswordWhenPasswordIsBlank() {
        Long id = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("   ");

        User existingUser = new User();
        existingUser.setPassword("oldPass");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toResponseDto(any())).thenReturn(new UserResponse());

        userService.updateUser(id, request);

        assertThat(existingUser.getPassword()).isEqualTo("oldPass");
    }

    @Test
    void updateUserShouldThrowUserNotFoundExceptionWhenIdDoesNotExist() {
        Long id = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(id, updateRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserByIdShouldDeactivateUserWhenUserIsBaseClassWithNoTrainings() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUserById(userId);

        assertAll(
                () -> assertThat(user.isActive()).isFalse(),
                () -> verify(userRepository).save(user),
                () -> verifyNoInteractions(trainingRepository)
        );
    }

    @Test
    void deleteUserByIdShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> userService.deleteUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null");

        verifyNoInteractions(userRepository, trainingRepository);
    }

    @Test
    void deleteUserByIdShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteUserByIdShouldThrowDataIntegrityViolationExceptionWhenUserIsTraineeWithFutureTrainings() {
        Long userId = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(userId);

        Training futureTraining = new Training();
        futureTraining.setTrainingDate(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(userId)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTraineeId(userId)).thenReturn(List.of(futureTraining));

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Cannot delete user with id 1 because they have future trainings");
    }

    @Test
    void deleteUserByIdShouldThrowDataIntegrityViolationExceptionWhenUserIsTrainerWithFutureTrainings() {
        Long userId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(userId);

        Training futureTraining = new Training();
        futureTraining.setTrainingDate(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(userId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainerId(userId)).thenReturn(List.of(futureTraining));

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Cannot delete user with id 1 because they have future trainings");
    }

    @Test
    void deleteUserByIdShouldDeactivateUserWhenUserIsTraineeWithOnlyPastTrainings() {
        Long userId = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(userId);
        trainee.setActive(true);

        Training pastTraining = new Training();
        pastTraining.setTrainingDate(LocalDateTime.now().minusDays(5));

        when(userRepository.findById(userId)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTraineeId(userId)).thenReturn(List.of(pastTraining));
        when(userRepository.save(trainee)).thenReturn(trainee);

        userService.deleteUserById(userId);

        assertAll(
                () -> assertThat(trainee.isActive()).isFalse(),
                () -> verify(userRepository).save(trainee)
        );
    }

    @Test
    void deleteUserByIdShouldDeactivateUserWhenUserIsTrainerWithOnlyPastTrainings() {
        Long userId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(userId);
        trainer.setActive(true);

        Training pastTraining = new Training();
        pastTraining.setTrainingDate(LocalDateTime.now().minusDays(5));

        when(userRepository.findById(userId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainerId(userId)).thenReturn(List.of(pastTraining));
        when(userRepository.save(trainer)).thenReturn(trainer);

        userService.deleteUserById(userId);

        assertAll(
                () -> assertThat(trainer.isActive()).isFalse(),
                () -> verify(userRepository).save(trainer)
        );
    }

    @Test
    void authenticateShouldPassWhenCredentialsAreValidAndUserIsActive() {
        LoginRequest request = new LoginRequest("john.doe", "securePassword123");

        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("securePassword123");
        user.setActive(true);

        when(userRepository.findByUsernameAndActiveTrue("john.doe")).thenReturn(Optional.of(user));

        userService.authenticate(request);

        verify(userRepository).findByUsernameAndActiveTrue("john.doe");
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenUsernameNotFound() {
        LoginRequest request = new LoginRequest("unknown.user", "anyPassword");

        when(userRepository.findByUsernameAndActiveTrue("unknown.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password.");
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest("john.doe", "wrongPassword");

        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("correctPassword");
        user.setActive(true);

        when(userRepository.findByUsernameAndActiveTrue("john.doe")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.authenticate(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password.");
    }

    @Test
    void getByUsernameShouldReturnUserWhenUsernameExistsAndIsActive() {
        String username = "john.doe";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setActive(true);

        when(userRepository.findByUsernameAndActiveTrue(username)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getByUsername(username);

        assertAll(
                () -> assertThat(actualUser).isNotNull(),
                () -> assertThat(actualUser.getUsername()).isEqualTo(username),
                () -> verify(userRepository).findByUsernameAndActiveTrue(username)
        );
    }

    @Test
    void getByUsernameShouldThrowIllegalArgumentExceptionWhenUsernameIsNull() {
        assertThatThrownBy(() -> userService.getByUsername(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be empty");

        verifyNoInteractions(userRepository);
    }

    @Test
    void getByUsernameShouldThrowIllegalArgumentExceptionWhenUsernameIsEmpty() {
        assertThatThrownBy(() -> userService.getByUsername(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be empty");

        verifyNoInteractions(userRepository);
    }

    @Test
    void getByUsernameShouldThrowIllegalArgumentExceptionWhenUsernameIsBlank() {
        assertThatThrownBy(() -> userService.getByUsername("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be empty");

        verifyNoInteractions(userRepository);
    }

    @Test
    void getByUsernameShouldThrowUserNotFoundExceptionWhenUserDoesNotExistOrIsInactive() {
        String username = "missing.user";

        when(userRepository.findByUsernameAndActiveTrue(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByUsername(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with username: missing.user");
    }
}
