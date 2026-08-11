package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.EntityAlreadyExistsException;
import com.gymtraining.application.exception.TrainerNotFoundException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.mapper.TrainerMapper;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.repository.TrainerRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.TrainingTypeRepository;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.service.impl.TrainerServiceImpl;
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
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private final String USERNAME = "john.doe";

    @Test
    void createTrainerFromRequestShouldThrowIllegalArgumentExceptionWhenUsernameAlreadyExists() {
        TrainerCreationRequest request = new TrainerCreationRequest();
        request.setUsername("pro_coach");

        when(userRepository.existsByUsername("pro_coach")).thenReturn(true);

        assertThatThrownBy(() -> trainerService.createTrainerFromRequest(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("Username 'pro_coach' is already taken!");

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void createTrainerFromRequestShouldThrowTrainingTypeNotFoundExceptionWhenSpecializationDoesNotExist() {
        TrainerCreationRequest request = new TrainerCreationRequest();
        request.setUsername("new_coach");
        request.setSpecializationId(99L);

        when(userRepository.existsByUsername("new_coach")).thenReturn(false);
        when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.createTrainerFromRequest(request))
                .isInstanceOf(TrainingTypeNotFoundException.class)
                .hasMessage("Training type not found");
    }

    @Test
    void createTrainerFromRequestShouldReturnTrainerResponseWhenRequestIsValid() {
        TrainerCreationRequest request = new TrainerCreationRequest();
        request.setUsername("coach_ivan");
        request.setSpecializationId(1L);

        TrainingType type = new TrainingType(1L, "Yoga");
        Trainer trainerFromMapper = new Trainer();
        Trainer savedTrainer = new Trainer();
        TrainerResponse expectedResponse = new TrainerResponse();

        when(userRepository.existsByUsername("coach_ivan")).thenReturn(false);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(trainerMapper.toTrainer(request)).thenReturn(trainerFromMapper);
        when(trainerRepository.save(trainerFromMapper)).thenReturn(savedTrainer);
        when(trainerMapper.toResponseDto(savedTrainer)).thenReturn(expectedResponse);

        TrainerResponse actualResponse = trainerService.createTrainerFromRequest(request);

        assertAll("Verify trainer creation details",
                () -> assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse),
                () -> verify(trainerRepository).save(trainerFromMapper),
                () -> assertThat(trainerFromMapper.getSpecialization()).isEqualTo(type),
                () -> assertThat(trainerFromMapper.isActive()).isTrue()
        );
    }

    @Test
    void getTrainerByIdShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        Long id = 1L;
        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerById(id))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer not found");
    }

    @Test
    void getTrainerByIdShouldReturnTrainerResponseDTOWhenTrainerExists() {
        Long trainerId = 1L;
        Trainer trainer = new Trainer();
        TrainerResponse expectedResponse = new TrainerResponse();

        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponseDto(trainer)).thenReturn(expectedResponse);

        TrainerResponse actualResponse = trainerService.getTrainerById(trainerId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void registerTrainerShouldReturnCredentialsResponseWhenRequestIsValid() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Alex", "Smith", 10L);
        TrainingType specialization = new TrainingType(10L, "Fitness");
        Trainer trainer = new Trainer();

        when(trainingTypeRepository.findById(10L)).thenReturn(Optional.of(specialization));
        when(trainerMapper.toTrainerFromRegister(request)).thenReturn(trainer);
        when(usernameGenerator.generate("Alex", "Smith")).thenReturn("alex.smith");
        when(passwordGenerator.generateRandom()).thenReturn("qwerty1234");
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        CredentialsResponse response = trainerService.registerTrainer(request);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.username()).isEqualTo("alex.smith"),
                () -> assertThat(response.password()).isEqualTo("qwerty1234"),
                () -> assertThat(trainer.getUsername()).isEqualTo("alex.smith"),
                () -> assertThat(trainer.getPassword()).isEqualTo("qwerty1234"),
                () -> assertThat(trainer.getSpecialization()).isEqualTo(specialization),
                () -> assertThat(trainer.isActive()).isTrue(),
                () -> verify(trainerRepository).save(trainer)
        );
    }

    @Test
    void registerTrainerShouldThrowTrainingTypeNotFoundExceptionWhenSpecializationDoesNotExist() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Alex", "Smith", 10L);

        when(trainingTypeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.registerTrainer(request))
                .isInstanceOf(TrainingTypeNotFoundException.class)
                .hasMessage("Training type not found");
    }

    @Test
    void getTrainerProfileByUsernameShouldReturnTrainerProfileResponseWhenTrainerExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        TraineeInfoResponse traineeInfo = mock(TraineeInfoResponse.class);
        List<TraineeInfoResponse> trainees = List.of(traineeInfo);
        TrainerProfileResponse expectedResponse = mock(TrainerProfileResponse.class);

        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(trainingService.fetchTraineesByTrainerId(1L)).thenReturn(trainees);
        when(trainerMapper.toProfileResponse(trainer, trainees)).thenReturn(expectedResponse);

        TrainerProfileResponse actualResponse = trainerService.getTrainerProfileByUsername(USERNAME);

        assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    void getTrainerProfileByUsernameShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerProfileByUsername(USERNAME))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer profile not found");
    }

    @Test
    void updateTrainerProfileShouldReturnTrainerUpdateResponseWhenTrainerExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        TrainerUpdateRequest request = new TrainerUpdateRequest("Alex", "Smith", true);
        TraineeInfoResponse traineeInfo = mock(TraineeInfoResponse.class);
        List<TraineeInfoResponse> trainees = List.of(traineeInfo);
        TrainerUpdateResponse expectedResponse = mock(TrainerUpdateResponse.class);

        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainingService.fetchTraineesByTrainerId(1L)).thenReturn(trainees);
        when(trainerMapper.toUpdateResponse(trainer, trainees)).thenReturn(expectedResponse);

        TrainerUpdateResponse actualResponse = trainerService.updateTrainerProfile(USERNAME, request);

        assertAll(
                () -> assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse),
                () -> verify(trainerMapper).updateTrainerFromRequest(request, trainer),
                () -> verify(trainerRepository).save(trainer)
        );
    }

    @Test
    void updateTrainerProfileShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        TrainerUpdateRequest request = new TrainerUpdateRequest("Alex", "Smith", true);
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainerProfile(USERNAME, request))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer profile not found");
    }

    @Test
    void getTrainerTrainingsShouldReturnFilteredTrainingsListWhenAllFiltersArePassed() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingDate(LocalDateTime.of(2026, 5, 15, 10, 0));

        TrainerTrainingResponse expectedResponse = TrainerTrainingResponse.builder()
                .traineeName("John Doe")
                .build();

        LocalDate fromDate = LocalDate.of(2026, 5, 14);
        LocalDate toDate = LocalDate.of(2026, 5, 16);
        LocalDateTime expectedFrom = fromDate.atStartOfDay();
        LocalDateTime expectedTo = toDate.atTime(23, 59, 59);

        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerRepository.findTrainerTrainingsByFilters(1L, expectedFrom, expectedTo, "John Doe"))
                .thenReturn(List.of(training));
        when(trainerMapper.toTrainerTrainingResponse(training, "John Doe")).thenReturn(expectedResponse);

        List<TrainerTrainingResponse> actualResult = trainerService.getTrainerTrainings(
                USERNAME, fromDate, toDate, "John Doe"
        );

        assertAll(
                () -> assertThat(actualResult).hasSize(1),
                () -> assertThat(actualResult.get(0)).isEqualTo(expectedResponse)
        );
        verify(trainerRepository).findTrainerTrainingsByFilters(1L, expectedFrom, expectedTo, "John Doe");
    }

    @Test
    void getTrainerTrainingsShouldReturnEmptyListWhenNoTrainingsMatchFilters() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerRepository.findTrainerTrainingsByFilters(1L, null, null, "Alex Smith"))
                .thenReturn(List.of());

        List<TrainerTrainingResponse> actualResult = trainerService.getTrainerTrainings(
                USERNAME, null, null, "Alex Smith"
        );

        assertThat(actualResult).isEmpty();
        verify(trainerRepository).findTrainerTrainingsByFilters(1L, null, null, "Alex Smith");
    }

    @Test
    void getTrainerTrainingsShouldPassNullDatesWhenFiltersAreNull() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);

        TrainerTrainingResponse expectedResponse = TrainerTrainingResponse.builder().build();

        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(trainerRepository.findTrainerTrainingsByFilters(1L, null, null, null))
                .thenReturn(List.of(training));
        when(trainerMapper.toTrainerTrainingResponse(training, "John Doe")).thenReturn(expectedResponse);

        List<TrainerTrainingResponse> actualResult = trainerService.getTrainerTrainings(
                USERNAME, null, null, null
        );

        assertThat(actualResult).hasSize(1).containsExactly(expectedResponse);
        verify(trainerRepository).findTrainerTrainingsByFilters(1L, null, null, null);
    }

    @Test
    void getTrainerTrainingsShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerTrainings(USERNAME, null, null, null))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer not found");
    }

    @Test
    void getTraineesByTrainerIdShouldReturnTraineeInfoResponseListWhenTrainerExists() {
        Long trainerId = 1L;
        TraineeInfoResponse traineeInfo = mock(TraineeInfoResponse.class);
        List<TraineeInfoResponse> expectedResponse = List.of(traineeInfo);

        when(trainerRepository.existsById(trainerId)).thenReturn(true);
        when(trainingService.fetchTraineesByTrainerId(trainerId)).thenReturn(expectedResponse);

        List<TraineeInfoResponse> actualResponse = trainerService.getTraineesByTrainerId(trainerId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void getTraineesByTrainerIdShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        Long trainerId = 1L;
        when(trainerRepository.existsById(trainerId)).thenReturn(false);

        assertThatThrownBy(() -> trainerService.getTraineesByTrainerId(trainerId))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer not found with id: 1");
    }

    @Test
    void deleteTrainerByIdShouldDeactivateTrainerWhenTrainerExistsAndHasNoFutureTrainings() {
        Long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
        trainer.setActive(true);

        Training pastTraining = new Training();
        pastTraining.setTrainer(trainer);
        pastTraining.setTrainingDate(LocalDateTime.now().minusDays(1));

        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainerId(trainerId)).thenReturn(List.of(pastTraining));

        trainerService.deleteTrainerById(trainerId);

        assertAll(
                () -> assertThat(trainer.isActive()).isFalse(),
                () -> verify(trainerRepository).save(trainer)
        );
    }

    @Test
    void deleteTrainerByIdShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        Long trainerId = 1L;
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.deleteTrainerById(trainerId))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer not found for this id: 1");
    }

    @Test
    void deleteTrainerByIdShouldThrowDataIntegrityViolationExceptionWhenTrainerHasFutureTrainings() {
        Long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        Training futureTraining = new Training();
        futureTraining.setTrainer(trainer);
        futureTraining.setTrainingDate(LocalDateTime.now().plusDays(2));

        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainerId(trainerId)).thenReturn(List.of(futureTraining));

        assertThatThrownBy(() -> trainerService.deleteTrainerById(trainerId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Cannot delete trainer with id 1 because they have assigned trainings");
    }
}
