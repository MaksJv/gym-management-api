package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

class TraineeMapperTest {

    private TraineeMapper traineeMapper;
    private TraineeCreationRequest creationRequest;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        traineeMapper = new TraineeMapperImpl();

        creationRequest = new TraineeCreationRequest();
        creationRequest.setFirstName("John");
        creationRequest.setLastName("Doe");
        creationRequest.setUsername("jdoe");
        creationRequest.setPassword("password123");
        creationRequest.setDateOfBirth(LocalDate.of(2000, 1, 1));
        creationRequest.setAddress("Street 1");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("jdoe");
        trainee.setPassword("password123");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Street 1");
        trainee.setActive(true);
    }

    @Test
    void toResponseDtoShouldMapCorrectly() {
        TraineeResponse response = traineeMapper.toResponseDto(trainee);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(trainee.getId()),
                () -> assertThat(response.getFirstName()).isEqualTo(trainee.getFirstName()),
                () -> assertThat(response.getLastName()).isEqualTo(trainee.getLastName()),
                () -> assertThat(response.getUsername()).isEqualTo(trainee.getUsername()),
                () -> assertThat(response.getDateOfBirth()).isEqualTo(trainee.getDateOfBirth()),
                () -> assertThat(response.getAddress()).isEqualTo(trainee.getAddress()),
                () -> assertThat(response.isActive()).isTrue()
        );
    }

    @Test
    void toResponseDtoShouldReturnNullWhenArgumentIsNull() {
        assertThat(traineeMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toTrainingResponseShouldMapCorrectlyWhenAllArgumentsProvided() {
        TrainingType type = new TrainingType();
        type.setName("Cardio");

        Training training = new Training();
        training.setTrainingName("Morning Run");
        training.setTrainingDate(LocalDateTime.of(2026, 5, 16, 10, 0));
        training.setDurationMinutes(45);
        training.setTrainingType(type);

        String trainerUsername = "alex.smith";

        TraineeTrainingResponse response = traineeMapper.toTrainingResponse(training, trainerUsername);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.trainingName()).isEqualTo("Morning Run"),
                () -> assertThat(response.trainingDate()).isEqualTo(LocalDateTime.of(2026, 5, 16, 10, 0)),
                () -> assertThat(response.trainingType()).isEqualTo("Cardio"),
                () -> assertThat(response.duration()).isEqualTo(45),
                () -> assertThat(response.trainerName()).isEqualTo("alex.smith")
        );
    }

    @Test
    void toTrainingResponseShouldReturnNullWhenBothArgumentsAreNull() {
        assertThat(traineeMapper.toTrainingResponse(null, null)).isNull();
    }

    @Test
    void toTrainingResponseShouldMapOnlyTrainerNameWhenTrainingIsNull() {
        TraineeTrainingResponse response = traineeMapper.toTrainingResponse(null, "alex.smith");

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.trainerName()).isEqualTo("alex.smith"),
                () -> assertThat(response.trainingName()).isNull(),
                () -> assertThat(response.trainingType()).isNull()
        );
    }

    @Test
    void toTrainingResponseShouldMapWithoutTypeNameWhenTrainingTypeIsNull() {
        Training training = new Training();
        training.setTrainingName("Gym Session");
        training.setTrainingType(null);

        TraineeTrainingResponse response = traineeMapper.toTrainingResponse(training, null);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.trainingName()).isEqualTo("Gym Session"),
                () -> assertThat(response.trainingType()).isNull(),
                () -> assertThat(response.trainerName()).isNull()
        );
    }

    @Test
    void toProfileResponseShouldMapCorrectlyWhenAllArgumentsProvided() {
        TrainerInfoResponse trainerInfo = mock(TrainerInfoResponse.class);
        List<TrainerInfoResponse> trainers = List.of(trainerInfo);

        TraineeProfileResponse response = traineeMapper.toProfileResponse(trainee, trainers);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.firstName()).isEqualTo("John"),
                () -> assertThat(response.lastName()).isEqualTo("Doe"),
                () -> assertThat(response.dateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1)),
                () -> assertThat(response.address()).isEqualTo("Street 1"),
                () -> assertThat(response.active()).isTrue(),
                () -> assertThat(response.trainers()).hasSize(1).containsExactly(trainerInfo)
        );
    }

    @Test
    void toProfileResponseShouldReturnNullWhenBothArgumentsAreNull() {
        assertThat(traineeMapper.toProfileResponse(null, null)).isNull();
    }

    @Test
    void toProfileResponseShouldMapOnlyTrainersWhenTraineeIsNull() {
        TrainerInfoResponse trainerInfo = mock(TrainerInfoResponse.class);
        List<TrainerInfoResponse> trainers = List.of(trainerInfo);

        TraineeProfileResponse response = traineeMapper.toProfileResponse(null, trainers);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.firstName()).isNull(),
                () -> assertThat(response.trainers()).hasSize(1).containsExactly(trainerInfo)
        );
    }

    @Test
    void toTraineeShouldMapCreationRequestToTraineeEntity() {
        Trainee result = traineeMapper.toTrainee(creationRequest);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result.getFirstName()).isEqualTo(creationRequest.getFirstName()),
                () -> assertThat(result.getLastName()).isEqualTo(creationRequest.getLastName()),
                () -> assertThat(result.getUsername()).isEqualTo(creationRequest.getUsername()),
                () -> assertThat(result.getPassword()).isNull(),
                () -> assertThat(result.isActive()).isFalse(),
                () -> assertThat(result.getDateOfBirth()).isEqualTo(creationRequest.getDateOfBirth()),
                () -> assertThat(result.getAddress()).isEqualTo(creationRequest.getAddress())
        );
    }

    @Test
    void toTraineeShouldReturnNullWhenRequestIsNull() {
        assertThat(traineeMapper.toTrainee(null)).isNull();
    }

    @Test
    void toTraineeFromRegisterShouldMapRegistrationRequestAndSetConstantActive() {
        TraineeRegistrationRequest registerRequest = TraineeRegistrationRequest.builder()
                .firstName("Bob")
                .lastName("Builder")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .address("Builder Street 5")
                .build();

        Trainee result = traineeMapper.toTraineeFromRegister(registerRequest);

        assertThat(result).isNotNull();
        assertAll(
                () -> assertThat(result.getFirstName()).isEqualTo(registerRequest.firstName()),
                () -> assertThat(result.getLastName()).isEqualTo(registerRequest.lastName()),
                () -> assertThat(result.getDateOfBirth()).isEqualTo(registerRequest.dateOfBirth()),
                () -> assertThat(result.getAddress()).isEqualTo(registerRequest.address()),
                () -> assertThat(result.getId()).isNull(),
                () -> assertThat(result.getUsername()).isNull(),
                () -> assertThat(result.getPassword()).isNull(),
                () -> assertThat(result.isActive()).isTrue()
        );
    }

    @Test
    void toTraineeFromRegisterShouldReturnNullWhenRequestIsNull() {
        assertThat(traineeMapper.toTraineeFromRegister(null)).isNull();
    }

    @Test
    void toUpdateResponseShouldMapCorrectly() {
        TraineeUpdateResponse response = traineeMapper.toUpdateResponse(trainee);

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.username()).isEqualTo(trainee.getUsername()),
                () -> assertThat(response.firstName()).isEqualTo(trainee.getFirstName()),
                () -> assertThat(response.lastName()).isEqualTo(trainee.getLastName()),
                () -> assertThat(response.active()).isTrue(),
                () -> assertThat(response.dateOfBirth()).isEqualTo(trainee.getDateOfBirth()),
                () -> assertThat(response.address()).isEqualTo(trainee.getAddress())
        );
    }

    @Test
    void toUpdateResponseShouldReturnNullWhenArgumentIsNull() {
        assertThat(traineeMapper.toUpdateResponse(null)).isNull();
    }

    @Test
    void updateTraineeFromRequestShouldModifyExistingTraineeFields() {
        TraineeUpdateRequest updateRequest = TraineeUpdateRequest.builder()
                .firstName("Alex")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1999, 9, 9))
                .address("Green Street 7")
                .build();

        traineeMapper.updateTraineeFromRequest(updateRequest, trainee);

        assertAll(
                () -> assertThat(trainee.getFirstName()).isEqualTo("Alex"),
                () -> assertThat(trainee.getLastName()).isEqualTo("Smith"),
                () -> assertThat(trainee.getDateOfBirth()).isEqualTo(LocalDate.of(1999, 9, 9)),
                () -> assertThat(trainee.getAddress()).isEqualTo("Green Street 7"),
                () -> assertThat(trainee.getId()).isEqualTo(1L),
                () -> assertThat(trainee.getUsername()).isEqualTo("jdoe"),
                () -> assertThat(trainee.getPassword()).isEqualTo("password123")
        );
    }

    @Test
    void updateTraineeFromRequestShouldNotModifyTraineeWhenRequestIsNull() {
        String originalAddress = trainee.getAddress();

        traineeMapper.updateTraineeFromRequest(null, trainee);

        assertThat(trainee.getAddress()).isEqualTo(originalAddress);
    }
}
