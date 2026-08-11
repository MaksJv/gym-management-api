package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.EntityAlreadyExistsException;
import com.gymtraining.application.exception.TraineeNotFoundException;
import com.gymtraining.application.mapper.TraineeMapper;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.repository.TraineeRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.service.impl.TraineeServiceImpl;
import com.gymtraining.application.util.PasswordGenerator;
import com.gymtraining.application.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private final String USERNAME = "john.doe";
    private final Long TRAINEE_ID = 1L;

    @Test
    void createTraineeFromRequestShouldThrowEntityAlreadyExistsExceptionWhenUsernameAlreadyExists() {
        TraineeCreationRequest request = new TraineeCreationRequest();
        request.setUsername("lucky_shadow");

        when(userRepository.existsByUsername("lucky_shadow")).thenReturn(true);

        assertThatThrownBy(() -> traineeService.createTraineeFromRequest(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("Username 'lucky_shadow' is already taken!");

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void createTraineeFromRequestShouldReturnTraineeResponseWhenRequestIsValid() {
        TraineeCreationRequest request = new TraineeCreationRequest();
        request.setUsername("new_user");

        Trainee trainee = new Trainee();
        Trainee savedTrainee = new Trainee();
        TraineeResponse expectedResponse = new TraineeResponse();

        when(userRepository.existsByUsername("new_user")).thenReturn(false);
        when(traineeMapper.toTrainee(request)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(savedTrainee);
        when(traineeMapper.toResponseDto(savedTrainee)).thenReturn(expectedResponse);

        TraineeResponse actualResponse = traineeService.createTraineeFromRequest(request);

        assertAll(
                () -> assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse),
                () -> assertThat(trainee.isActive()).isTrue(),
                () -> verify(traineeRepository).save(trainee)
        );
    }

    @Test
    void getTraineeByIdShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTraineeById(TRAINEE_ID))
                .isInstanceOf(TraineeNotFoundException.class)
                .hasMessage("Trainee not found");
    }

    @Test
    void getTraineeByIdShouldReturnTraineeResponseWhenTraineeExists() {
        Trainee trainee = new Trainee();
        TraineeResponse expectedResponse = new TraineeResponse();

        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponseDto(trainee)).thenReturn(expectedResponse);

        TraineeResponse actualResponse = traineeService.getTraineeById(TRAINEE_ID);

        assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    void registerTraineeShouldReturnCredentialsResponseWhenRequestIsValid() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest("Mike", "Smith", LocalDate.now(), "Address 1");
        Trainee trainee = new Trainee();

        when(traineeMapper.toTraineeFromRegister(request)).thenReturn(trainee);
        when(usernameGenerator.generate("Mike", "Smith")).thenReturn("mike.smith");
        when(passwordGenerator.generateRandom()).thenReturn("pass123");

        CredentialsResponse response = traineeService.registerTrainee(request);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.username()).isEqualTo("mike.smith"),
                () -> assertThat(response.password()).isEqualTo("pass123"),
                () -> assertThat(trainee.getUsername()).isEqualTo("mike.smith"),
                () -> assertThat(trainee.getPassword()).isEqualTo("pass123"),
                () -> assertThat(trainee.isActive()).isTrue(),
                () -> verify(traineeRepository).save(trainee)
        );
    }

    @Test
    void getTraineeProfileByUsernameShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTraineeProfileByUsername(USERNAME))
                .isInstanceOf(TraineeNotFoundException.class)
                .hasMessage("Trainee profile not found for user: " + USERNAME);
    }

    @Test
    void getTraineeProfileByUsernameShouldReturnProfileWithTrainersWhenTraineeExists() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);

        TrainingType specialization = new TrainingType(1L, "Yoga");
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.alex");
        trainer.setFirstName("Alex");
        trainer.setLastName("Smith");
        trainer.setSpecialization(specialization);

        Training training = new Training();
        training.setTrainer(trainer);

        TraineeProfileResponse expectedProfile = mock(TraineeProfileResponse.class);

        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTraineeId(TRAINEE_ID)).thenReturn(List.of(training, training));
        when(traineeMapper.toProfileResponse(any(Trainee.class), any())).thenReturn(expectedProfile);

        TraineeProfileResponse actualProfile = traineeService.getTraineeProfileByUsername(USERNAME);

        assertThat(actualProfile).isEqualTo(expectedProfile);
        verify(traineeMapper).toProfileResponse(argThat(t -> t.equals(trainee)), argThat(list -> {
            assertThat(list).hasSize(1);
            TrainerInfoResponse info = list.get(0);
            return info.username().equals("trainer.alex") && info.specialization().equals("Yoga");
        }));
    }

    @Test
    void updateTraineeProfileShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        TraineeUpdateRequest request = new TraineeUpdateRequest("John", "Doe", LocalDate.now(), "Addr", true);
        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTraineeProfile(USERNAME, request))
                .isInstanceOf(TraineeNotFoundException.class)
                .hasMessage("Trainee profile not found for: " + USERNAME);
    }

    @Test
    void updateTraineeProfileShouldReturnUpdateResponseWhenTraineeExists() {
        TraineeUpdateRequest request = new TraineeUpdateRequest("John", "Doe", LocalDate.now(), "Addr", true);
        Trainee trainee = new Trainee();
        Trainee updatedTrainee = new Trainee();
        TraineeUpdateResponse expectedResponse = mock(TraineeUpdateResponse.class);

        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(updatedTrainee);
        when(traineeMapper.toUpdateResponse(updatedTrainee)).thenReturn(expectedResponse);

        TraineeUpdateResponse actualResponse = traineeService.updateTraineeProfile(USERNAME, request);

        assertAll(
                () -> assertThat(actualResponse).isEqualTo(expectedResponse),
                () -> verify(traineeMapper).updateTraineeFromRequest(request, trainee),
                () -> verify(traineeRepository).save(trainee)
        );
    }

    @Test
    void deleteTraineeByUsernameShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.deleteTraineeByUsername(USERNAME))
                .isInstanceOf(TraineeNotFoundException.class);
    }

    @Test
    void deleteTraineeByUsernameShouldDeleteTrainingsAndTraineeWhenTraineeExists() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);

        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeByUsername(USERNAME);

        assertAll(
                () -> verify(trainingRepository).deleteByTraineeId(TRAINEE_ID),
                () -> verify(traineeRepository).delete(trainee)
        );
    }

    @Test
    void getTraineeTrainingsShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTraineeTrainings(USERNAME, null, null, null, null))
                .isInstanceOf(TraineeNotFoundException.class);
    }

    @Test
    void getTraineeTrainingsShouldReturnFilteredTrainingsList() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.alex");

        TrainingType type = new TrainingType(1L, "Cardio");

        Training matchingTraining = new Training();
        matchingTraining.setTrainingDate(LocalDateTime.of(2026, 5, 20, 10, 0));
        matchingTraining.setTrainer(trainer);
        matchingTraining.setTrainingType(type);

        TraineeTrainingResponse responseDto = TraineeTrainingResponse.builder()
                .trainingName("Cardio Class")
                .build();

        LocalDate fromDate = LocalDate.of(2026, 5, 19);
        LocalDate toDate = LocalDate.of(2026, 5, 21);

        LocalDateTime expectedFrom = fromDate.atStartOfDay();
        LocalDateTime expectedTo = toDate.atTime(23, 59, 59);

        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));

        when(traineeRepository.findTrainingsByFilters(
                TRAINEE_ID, expectedFrom, expectedTo, "Cardio", "trainer.alex"
        )).thenReturn(List.of(matchingTraining));

        when(traineeMapper.toTrainingResponse(matchingTraining, "trainer.alex")).thenReturn(responseDto);

        List<TraineeTrainingResponse> result = traineeService.getTraineeTrainings(
                USERNAME,
                fromDate,
                toDate,
                "trainer.alex",
                "Cardio"
        );

        assertThat(result).hasSize(1).containsExactly(responseDto);

        verify(traineeRepository).findTrainingsByFilters(
                TRAINEE_ID, expectedFrom, expectedTo, "Cardio", "trainer.alex"
        );
    }

    @Test
    void getTraineeTrainingsShouldReturnAllTrainingsWhenAllFiltersAreNull() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.alex");

        Training training = new Training();
        training.setTrainingDate(LocalDateTime.of(2026, 5, 20, 10, 0));
        training.setTrainer(trainer);

        TraineeTrainingResponse responseDto = TraineeTrainingResponse.builder().build();

        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));

        when(traineeRepository.findTrainingsByFilters(TRAINEE_ID, null, null, null, null))
                .thenReturn(List.of(training));

        when(traineeMapper.toTrainingResponse(training, "trainer.alex")).thenReturn(responseDto);

        List<TraineeTrainingResponse> result = traineeService.getTraineeTrainings(
                USERNAME, null, null, null, null
        );

        assertThat(result).hasSize(1).containsExactly(responseDto);
        verify(traineeRepository).findTrainingsByFilters(TRAINEE_ID, null, null, null, null);
    }

    @Test
    void getTrainersByTraineeIdShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTrainersByTraineeId(TRAINEE_ID))
                .isInstanceOf(TraineeNotFoundException.class);
    }

    @Test
    void getTrainersByTraineeIdShouldReturnUniqueTrainersList() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);

        TrainingType specialization = new TrainingType(1L, "Fitness");
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.john");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setSpecialization(specialization);

        Training t1 = new Training();
        t1.setTrainer(trainer);

        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTraineeId(TRAINEE_ID)).thenReturn(List.of(t1, t1));

        List<TrainerInfoResponse> result = traineeService.getTrainersByTraineeId(TRAINEE_ID);

        assertThat(result).hasSize(1);
        TrainerInfoResponse info = result.get(0);
        assertAll(
                () -> assertThat(info.username()).isEqualTo("trainer.john"),
                () -> assertThat(info.firstName()).isEqualTo("John"),
                () -> assertThat(info.lastName()).isEqualTo("Doe"),
                () -> assertThat(info.specialization()).isEqualTo("Fitness")
        );
    }

    @Test
    void deleteTraineeByIdShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.deleteTraineeById(TRAINEE_ID))
                .isInstanceOf(TraineeNotFoundException.class)
                .hasMessage("Trainee not found with id: " + TRAINEE_ID);
    }

    @Test
    void deleteTraineeByIdShouldThrowDataIntegrityViolationExceptionWhenTraineeHasFutureTrainings() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);

        Training futureTraining = new Training();
        futureTraining.setTrainingDate(LocalDateTime.now().plusDays(5));

        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTraineeId(TRAINEE_ID)).thenReturn(List.of(futureTraining));

        assertThatThrownBy(() -> traineeService.deleteTraineeById(TRAINEE_ID))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Cannot delete trainee with id 1 because they have assigned trainings");

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void deleteTraineeByIdShouldDeactivateTraineeWhenNoFutureTrainingsExist() {
        Trainee trainee = new Trainee();
        trainee.setId(TRAINEE_ID);
        trainee.setActive(true);

        Training pastTraining = new Training();
        pastTraining.setTrainingDate(LocalDateTime.now().minusDays(5));

        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTraineeId(TRAINEE_ID)).thenReturn(List.of(pastTraining));

        traineeService.deleteTraineeById(TRAINEE_ID);

        assertAll(
                () -> assertThat(trainee.isActive()).isFalse(),
                () -> verify(traineeRepository).save(trainee)
        );
    }
}
