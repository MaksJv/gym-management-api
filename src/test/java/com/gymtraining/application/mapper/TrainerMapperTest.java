package com.gymtraining.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.List;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainerMapperTest {

    private TrainerMapper trainerMapper;
    private TrainerCreationRequest creationRequest;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainerMapper = new TrainerMapperImpl();

        creationRequest = new TrainerCreationRequest();
        creationRequest.setFirstName("Alice");
        creationRequest.setLastName("Smith");
        creationRequest.setUsername("asmith");
        creationRequest.setPassword("strongPassword123");
        creationRequest.setSpecializationId(5L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(5L);
        trainingType.setName("Yoga");

        trainer = new Trainer();
        trainer.setId(10L);
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setUsername("asmith");
        trainer.setPassword("strongPassword123");
        trainer.setSpecialization(trainingType);
        trainer.setActive(true);
    }

    @Test
    void toResponseDtoShouldMapCorrectlyWhenAllDataPresent() {
        TrainerResponse response = trainerMapper.toResponseDto(trainer);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(trainer.getId()),
                () -> assertThat(response.getFirstName()).isEqualTo(trainer.getFirstName()),
                () -> assertThat(response.getLastName()).isEqualTo(trainer.getLastName()),
                () -> assertThat(response.getUsername()).isEqualTo(trainer.getUsername()),
                () -> assertThat(response.getSpecialization()).isEqualTo("Yoga"),
                () -> assertThat(response.isActive()).isTrue()
        );
    }

    @Test
    void toResponseDtoShouldReturnNullWhenArgumentIsNull() {
        assertThat(trainerMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoShouldHandleNullSpecialization() {
        trainer.setSpecialization(null);
        TrainerResponse response = trainerMapper.toResponseDto(trainer);

        assertThat(response).isNotNull();
        assertThat(response.getSpecialization()).isNull();
    }

    @Test
    void toProfileResponseShouldMapAllPropertiesWhenAllArgumentsProvided() {
        TraineeInfoResponse traineeInfo = mock(TraineeInfoResponse.class);
        List<TraineeInfoResponse> trainees = List.of(traineeInfo);

        TrainerProfileResponse response = trainerMapper.toProfileResponse(trainer, trainees);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.firstName()).isEqualTo("Alice"),
                () -> assertThat(response.lastName()).isEqualTo("Smith"),
                () -> assertThat(response.specialization()).isEqualTo("Yoga"),
                () -> assertThat(response.active()).isTrue(),
                () -> assertThat(response.trainees()).hasSize(1).containsExactly(traineeInfo)
        );
    }

    @Test
    void toProfileResponseShouldReturnNullWhenAllArgumentsAreNull() {
        assertThat(trainerMapper.toProfileResponse(null, null)).isNull();
    }

    @Test
    void toProfileResponseShouldMapOnlyTraineesWhenTrainerIsNull() {
        TraineeInfoResponse traineeInfo = mock(TraineeInfoResponse.class);
        List<TraineeInfoResponse> trainees = List.of(traineeInfo);

        TrainerProfileResponse response = trainerMapper.toProfileResponse(null, trainees);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.trainees()).hasSize(1).containsExactly(traineeInfo),
                () -> assertThat(response.firstName()).isNull(),
                () -> assertThat(response.specialization()).isNull()
        );
    }

    @Test
    void toTrainerTrainingResponseShouldMapCorrectlyWhenAllArgumentsProvided() {
        TrainingType type = new TrainingType();
        type.setName("CrossFit");

        Training training = new Training();
        training.setTrainingName("Power Session");
        training.setTrainingDate(LocalDateTime.of(2026, 6, 20, 14, 30));
        training.setDurationMinutes(90);
        training.setTrainingType(type);

        TrainerTrainingResponse response = trainerMapper.toTrainerTrainingResponse(training, "John Doe");

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.trainingName()).isEqualTo("Power Session"),
                () -> assertThat(response.trainingDate()).isEqualTo(LocalDateTime.of(2026, 6, 20, 14, 30)),
                () -> assertThat(response.trainingType()).isEqualTo("CrossFit"),
                () -> assertThat(response.duration()).isEqualTo(90),
                () -> assertThat(response.traineeName()).isEqualTo("John Doe")
        );
    }

    @Test
    void toTrainerTrainingResponseShouldReturnNullWhenBothArgumentsAreNull() {
        assertThat(trainerMapper.toTrainerTrainingResponse(null, null)).isNull();
    }

    @Test
    void toTrainerTrainingResponseShouldMapOnlyTraineeFullNameWhenTrainingIsNull() {
        TrainerTrainingResponse response = trainerMapper.toTrainerTrainingResponse(null, "John Doe");

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.traineeName()).isEqualTo("John Doe"),
                () -> assertThat(response.trainingName()).isNull(),
                () -> assertThat(response.trainingType()).isNull()
        );
    }

    @Test
    void toTrainerTrainingResponseShouldMapSuccessfullyWhenTrainingTypeIsNull() {
        Training training = new Training();
        training.setTrainingName("Yoga Session");
        training.setTrainingDate(LocalDateTime.of(2026, 5, 16, 12, 0));
        training.setDurationMinutes(60);
        training.setTrainingType(null);

        TrainerTrainingResponse response = trainerMapper.toTrainerTrainingResponse(training, "John Doe");

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.trainingName()).isEqualTo("Yoga Session"),
                () -> assertThat(response.trainingType()).isNull(),
                () -> assertThat(response.duration()).isEqualTo(60),
                () -> assertThat(response.traineeName()).isEqualTo("John Doe")
        );
    }

    @Test
    void toUpdateResponseShouldMapAllPropertiesWhenAllArgumentsProvided() {
        TraineeInfoResponse traineeInfo = mock(TraineeInfoResponse.class);
        List<TraineeInfoResponse> trainees = List.of(traineeInfo);

        TrainerUpdateResponse response = trainerMapper.toUpdateResponse(trainer, trainees);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.username()).isEqualTo("asmith"),
                () -> assertThat(response.firstName()).isEqualTo("Alice"),
                () -> assertThat(response.lastName()).isEqualTo("Smith"),
                () -> assertThat(response.specialization()).isEqualTo("Yoga"),
                () -> assertThat(response.active()).isTrue(),
                () -> assertThat(response.trainees()).hasSize(1).containsExactly(traineeInfo)
        );
    }

    @Test
    void toUpdateResponseShouldReturnNullWhenAllArgumentsAreNull() {
        assertThat(trainerMapper.toUpdateResponse(null, null)).isNull();
    }

    @Test
    void toTrainerShouldMapCreationRequestToTrainerEntity() {
        Trainer result = trainerMapper.toTrainer(creationRequest);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result.getFirstName()).isEqualTo(creationRequest.getFirstName()),
                () -> assertThat(result.getLastName()).isEqualTo(creationRequest.getLastName()),
                () -> assertThat(result.getUsername()).isEqualTo(creationRequest.getUsername()),
                () -> assertThat(result.getId()).isNull(),
                () -> assertThat(result.getPassword()).isNull(),
                () -> assertThat(result.getSpecialization()).isNull()
        );
    }

    @Test
    void toTrainerShouldReturnNullWhenRequestIsNull() {
        assertThat(trainerMapper.toTrainer((TrainerCreationRequest) null)).isNull();
    }

    @Test
    void updateTrainerFromRequestShouldModifyFieldsExceptIgnored() {
        TrainerUpdateRequest updateRequest = TrainerUpdateRequest.builder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .build();

        trainerMapper.updateTrainerFromRequest(updateRequest, trainer);

        assertAll(
                () -> assertThat(trainer.getFirstName()).isEqualTo("NewFirst"),
                () -> assertThat(trainer.getLastName()).isEqualTo("NewLast"),
                () -> assertThat(trainer.getId()).isEqualTo(10L),
                () -> assertThat(trainer.getUsername()).isEqualTo("asmith"),
                () -> assertThat(trainer.getPassword()).isEqualTo("strongPassword123"),
                () -> assertThat(trainer.getSpecialization().getName()).isEqualTo("Yoga")
        );
    }

    @Test
    void updateTrainerFromRequestShouldNotModifyTrainerWhenRequestIsNull() {
        String originalFirstName = trainer.getFirstName();
        trainerMapper.updateTrainerFromRequest(null, trainer);
        assertThat(trainer.getFirstName()).isEqualTo(originalFirstName);
    }

    @Test
    void toTrainerFromRegisterShouldMapCorrectly() {
        TrainerRegistrationRequest registerRequest = TrainerRegistrationRequest.builder()
                .firstName("Bob")
                .lastName("Builder")
                .specializationId(3L)
                .build();

        Trainer result = trainerMapper.toTrainerFromRegister(registerRequest);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result.getFirstName()).isEqualTo("Bob"),
                () -> assertThat(result.getLastName()).isEqualTo("Builder"),
                () -> assertThat(result.getId()).isNull(),
                () -> assertThat(result.getUsername()).isNull(),
                () -> assertThat(result.getPassword()).isNull(),
                () -> assertThat(result.isActive()).isFalse(),
                () -> assertThat(result.getSpecialization()).isNull()
        );
    }

    @Test
    void toTrainerFromRegisterShouldReturnNullWhenRequestIsNull() {
        assertThat(trainerMapper.toTrainerFromRegister(null)).isNull();
    }
}
