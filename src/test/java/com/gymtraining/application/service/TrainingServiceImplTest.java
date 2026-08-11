package com.gymtraining.application.service;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.TraineeNotFoundException;
import com.gymtraining.application.exception.TrainerNotFoundException;
import com.gymtraining.application.exception.TrainingNotFoundException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.mapper.TrainingMapper;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.repository.TraineeRepository;
import com.gymtraining.application.repository.TrainerRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.TrainingTypeRepository;
import com.gymtraining.application.service.impl.TrainingServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private static final Long TRAINEE_ID = 1L;
    private static final String USERNAME = "trainee.user";

    @Test
    void createTrainingFromRequestShouldSaveTrainingWhenRequestIsValid() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);
        request.setTrainingTypeId(3L);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(3L);
        trainingType.setName("Fitness");

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);

        Training training = new Training();
        Training savedTraining = new Training();

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(trainingType));
        when(trainingMapper.toEntity(request)).thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(savedTraining);

        Training result = trainingService.createTrainingFromRequest(request);

        assertAll(
                () -> assertThat(result).isNotNull().isEqualTo(savedTraining),
                () -> assertThat(training.getTrainee()).isEqualTo(trainee),
                () -> assertThat(training.getTrainer()).isEqualTo(trainer),
                () -> assertThat(training.getTrainingType()).isEqualTo(trainingType),
                () -> verify(trainingRepository).save(training)
        );
    }

    @Test
    void createTrainingFromRequestShouldThrowTraineeNotFoundExceptionWhenTraineeDoesNotExist() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);

        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.createTrainingFromRequest(request))
                .isInstanceOf(TraineeNotFoundException.class)
                .hasMessage("Trainee not found!");

        verifyNoInteractions(trainerRepository, trainingTypeRepository, trainingRepository);
    }

    @Test
    void createTrainingFromRequestShouldThrowIllegalStateExceptionWhenTraineeIsInactive() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setActive(false);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> trainingService.createTrainingFromRequest(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Trainee user with id: 1is not active");

        verifyNoInteractions(trainerRepository, trainingTypeRepository, trainingRepository);
    }

    @Test
    void createTrainingFromRequestShouldThrowTrainerNotFoundExceptionWhenTrainerDoesNotExist() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setActive(true);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.createTrainingFromRequest(request))
                .isInstanceOf(TrainerNotFoundException.class)
                .hasMessage("Trainer not found!");

        verifyNoInteractions(trainingTypeRepository, trainingRepository);
    }

    @Test
    void createTrainingFromRequestShouldThrowIllegalStateExceptionWhenTrainerIsInactive() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setActive(false);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));

        assertThatThrownBy(() -> trainingService.createTrainingFromRequest(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Trainer user with id: 2is not active");

        verifyNoInteractions(trainingTypeRepository, trainingRepository);
    }

    @Test
    void createTrainingFromRequestShouldThrowTrainingTypeNotFoundExceptionWhenTypeDoesNotExist() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);
        request.setTrainingTypeId(3L);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setActive(true);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.createTrainingFromRequest(request))
                .isInstanceOf(TrainingTypeNotFoundException.class)
                .hasMessage("Training Type not found!");

        verifyNoInteractions(trainingRepository);
    }

    @Test
    void createTrainingFromRequestShouldThrowIllegalStateExceptionWhenTrainerSpecializationDoesNotMatch() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);
        request.setTrainingTypeId(3L);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setActive(true);

        TrainingType trainerSpecialization = new TrainingType();
        trainerSpecialization.setName("Yoga");

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setActive(true);
        trainer.setSpecialization(trainerSpecialization);

        TrainingType requestedType = new TrainingType();
        requestedType.setName("Fitness");

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(requestedType));

        assertThatThrownBy(() -> trainingService.createTrainingFromRequest(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Trainer does not specialize in Fitness");

        verifyNoInteractions(trainingRepository);
    }

    @Test
    void getTrainingByIdShouldThrowTrainingNotFoundExceptionWhenTrainingDoesNotExist() {
        Long trainingId = 1L;
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.getTrainingById(trainingId))
                .isInstanceOf(TrainingNotFoundException.class)
                .hasMessage("Training not found");
    }

    @Test
    void getTrainingByIdShouldReturnTrainingResponseDTOWhenTrainingExists() {
        Long trainingId = 1L;
        Training training = new Training();
        training.setId(trainingId);

        TrainingResponse expectedResponse = new TrainingResponse();

        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        when(trainingMapper.toResponseDto(training)).thenReturn(expectedResponse);

        TrainingResponse actualResponse = trainingService.getTrainingById(trainingId);

        assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    void assignTrainee2TrainerShouldSaveAssignmentWhenDataIsValidAndUnique() {
        Long traineeId = 1L;
        Long trainerId = 2L;

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);
        trainee.setActive(true);

        TrainingType specialization = new TrainingType();
        specialization.setName("Fitness");

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
        trainer.setActive(true);
        trainer.setSpecialization(specialization);

        Training savedTraining = new Training();
        TrainingResponse expectedResponse = new TrainingResponse();

        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainerId(trainerId)).thenReturn(List.of());
        when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);
        when(trainingMapper.toResponseDto(savedTraining)).thenReturn(expectedResponse);

        TrainingResponse actualResponse = trainingService.assignTrainee2Trainer(traineeId, trainerId);

        assertAll(
                () -> assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse),
                () -> verify(trainingRepository).save(any(Training.class))
        );
    }

    @Test
    void assignTrainee2TrainerShouldThrowIllegalStateExceptionWhenAssignmentAlreadyExists() {
        Long traineeId = 1L;
        Long trainerId = 2L;

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);
        trainee.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
        trainer.setActive(true);

        Training existingTraining = new Training();
        existingTraining.setTrainee(trainee);

        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainerId(trainerId)).thenReturn(List.of(existingTraining));

        assertThatThrownBy(() -> trainingService.assignTrainee2Trainer(traineeId, trainerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Trainee with id 1 is already assigned to trainer with id 2");

        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void fetchTraineesByTrainerIdShouldReturnActiveDistinctTraineeResponses() {
        Long trainerId = 1L;

        Trainee trainee = new Trainee();
        trainee.setUsername("trainee.valery");
        trainee.setFirstName("Valery");
        trainee.setLastName("Meladze");
        trainee.setActive(true);

        when(trainingRepository.findActiveTraineesByTrainerId(trainerId))
                .thenReturn(List.of(trainee));

        List<TraineeInfoResponse> result = trainingService.fetchTraineesByTrainerId(trainerId);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).username()).isEqualTo("trainee.valery"),
                () -> assertThat(result.get(0).firstName()).isEqualTo("Valery"),
                () -> assertThat(result.get(0).lastName()).isEqualTo("Meladze")
        );
        verify(trainingRepository).findActiveTraineesByTrainerId(trainerId);
    }

    @Test
    void getTrainersByTraineeIdShouldReturnActiveDistinctTrainerResponses() {
        Long traineeId = 1L;

        TrainingType specialization = new TrainingType();
        specialization.setName("Boxing");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.bob");
        trainer.setFirstName("Bob");
        trainer.setLastName("Marley");
        trainer.setActive(true);
        trainer.setSpecialization(specialization);

        when(trainingRepository.findActiveTrainersByTraineeId(traineeId))
                .thenReturn(List.of(trainer));

        List<TrainerInfoResponse> result = trainingService.getTrainersByTraineeId(traineeId);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).username()).isEqualTo("trainer.bob"),
                () -> assertThat(result.get(0).firstName()).isEqualTo("Bob"),
                () -> assertThat(result.get(0).lastName()).isEqualTo("Marley"),
                () -> assertThat(result.get(0).specialization()).isEqualTo("Boxing")
        );
        verify(trainingRepository).findActiveTrainersByTraineeId(traineeId);
    }

    @Test
    void fetchTraineesByTrainerIdShouldReturnEmptyListWhenNoTraineesFound() {
        Long trainerId = 2L;
        when(trainingRepository.findActiveTraineesByTrainerId(trainerId))
                .thenReturn(List.of());

        List<TraineeInfoResponse> result = trainingService.fetchTraineesByTrainerId(trainerId);

        assertThat(result).isEmpty();
        verify(trainingRepository).findActiveTraineesByTrainerId(trainerId);
    }

    @Test
    void getTrainersByTraineeIdShouldReturnEmptyListWhenNoTrainersFound() {
        Long traineeId = 2L;
        when(trainingRepository.findActiveTrainersByTraineeId(traineeId))
                .thenReturn(List.of());

        List<TrainerInfoResponse> result = trainingService.getTrainersByTraineeId(traineeId);

        assertThat(result).isEmpty();
        verify(trainingRepository).findActiveTrainersByTraineeId(traineeId);
    }

    @Test
    void addTrainingShouldSaveTrainingWhenRequestIsValid() {
        TrainingAdditionRequest request = new TrainingAdditionRequest("student", "coach", "Name", LocalDateTime.now(), "Yoga", 10);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();
        Training training = new Training();

        when(traineeRepository.findByUsername("student")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("coach")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByNameIgnoreCase("Yoga")).thenReturn(Optional.of(type));
        when(trainingMapper.toEntityForAddition(request)).thenReturn(training);

        trainingService.addTraining(request);

        assertAll(
                () -> assertThat(training.getTrainee()).isEqualTo(trainee),
                () -> assertThat(training.getTrainer()).isEqualTo(trainer),
                () -> assertThat(training.getTrainingType()).isEqualTo(type),
                () -> verify(trainingRepository).save(training)
        );
    }

    @Test
    void getAssignmentByIdShouldReturnTrainingResponseDTOWhenAssignmentExists() {
        Long id = 1L;
        Training training = new Training();
        TrainingResponse expectedResponse = new TrainingResponse();

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));
        when(trainingMapper.toResponseDto(training)).thenReturn(expectedResponse);

        TrainingResponse actualResponse = trainingService.getAssignmentById(id);

        assertThat(actualResponse).isNotNull().isEqualTo(expectedResponse);
    }

    @Test
    void getAssignmentByIdShouldThrowTrainingNotFoundExceptionWhenAssignmentDoesNotExist() {
        Long id = 1L;
        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.getAssignmentById(id))
                .isInstanceOf(TrainingNotFoundException.class)
                .hasMessage("Training assignment not found with id: 1");
    }
}
