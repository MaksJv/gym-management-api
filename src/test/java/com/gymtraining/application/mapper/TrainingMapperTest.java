package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.TrainingAdditionRequest;
import com.gymtraining.application.dto.TrainingCreateRequest;
import com.gymtraining.application.dto.TrainingResponse;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import com.gymtraining.application.model.Trainer;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TrainingMapperTest {

    private TrainingMapper trainingMapper;
    private Training training;
    private TrainingCreateRequest request;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainingMapper = Mappers.getMapper(TrainingMapper.class);

        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setName("Strength");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        trainer = new Trainer();
        trainer.setId(2L);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        training = new Training();
        training.setId(100L);
        training.setTrainingName("Deadlift Session");
        training.setTrainingDate(LocalDateTime.of(2026, 5, 20, 10, 0));
        training.setDurationMinutes(60);
        training.setTrainingType(type);
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        request = new TrainingCreateRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);
        request.setTrainingName("Yoga");
        request.setTrainingDate(LocalDateTime.of(2026, 6, 1, 12, 0));
        request.setDurationMinutes(45);
        request.setTrainingTypeId(3L);
    }

    @Test
    void toResponseDtoShouldMapCorrectlyWhenAllDataPresent() {
        TrainingResponse response = trainingMapper.toResponseDto(training);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(training.getId()),
                () -> assertThat(response.getTrainingName()).isEqualTo(training.getTrainingName()),
                () -> assertThat(response.getTrainingDate()).isEqualTo(training.getTrainingDate()),
                () -> assertThat(response.getDuration()).isEqualTo(training.getDurationMinutes()),
                () -> assertThat(response.getTrainingTypeName()).isEqualTo("Strength"),
                () -> assertThat(response.getTraineeName()).isEqualTo("John Doe"),
                () -> assertThat(response.getTrainerName()).isEqualTo("Jane Smith")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTrainingTypeScenarios")
    void toResponseDtoShouldHandleVariousTrainingTypeStates(TrainingType type, String expectedName) {
        training.setTrainingType(type);
        TrainingResponse response = trainingMapper.toResponseDto(training);

        assertThat(response.getTrainingTypeName()).isEqualTo(expectedName);
    }

    private static Stream<Arguments> provideTrainingTypeScenarios() {
        TrainingType type = new TrainingType();
        type.setName("Cardio");

        return Stream.of(
                Arguments.of(type, "Cardio"),
                Arguments.of(null, null)
        );
    }

    @Test
    void toResponseDtoShouldReturnNullWhenTrainingIsNull() {
        assertThat(trainingMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtoShouldMapCorrectlyWhenTraineeAndTrainerAreNull() {
        training.setTrainee(null);
        training.setTrainer(null);

        TrainingResponse response = trainingMapper.toResponseDto(training);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getId()).isEqualTo(training.getId()),
                () -> assertThat(response.getTraineeName()).isNull(),
                () -> assertThat(response.getTrainerName()).isNull()
        );
    }

    @Test
    void toEntityShouldMapRequestToTraining() {
        Training result = trainingMapper.toEntity(request);

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isNull(),
                () -> assertThat(result.getTrainee()).isNull(),
                () -> assertThat(result.getTrainer()).isNull(),
                () -> assertThat(result.getTrainingType()).isNull(),
                () -> assertThat(result.getTrainingName()).isEqualTo(request.getTrainingName()),
                () -> assertThat(result.getTrainingDate()).isEqualTo(request.getTrainingDate()),
                () -> assertThat(result.getDurationMinutes()).isEqualTo(request.getDurationMinutes())
        );
    }

    @Test
    void toEntityShouldReturnNullWhenRequestIsNull() {
        assertThat(trainingMapper.toEntity(null)).isNull();
    }

    @Test
    void toEntityForAdditionShouldMapAllFieldsCorrectlyWhenDurationIsPresent() {
        LocalDateTime trainingDate = LocalDateTime.of(2026, 5, 16, 18, 0);

        TrainingAdditionRequest additionRequest = TrainingAdditionRequest.builder()
                .traineeUsername("john.doe")
                .trainerUsername("alex.smith")
                .trainingName("Leg Day")
                .trainingDate(trainingDate)
                .trainingType("Strength")
                .duration(60)
                .build();

        Training result = trainingMapper.toEntityForAddition(additionRequest);

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isNull(),
                () -> assertThat(result.getDurationMinutes()).isEqualTo(60),
                () -> assertThat(result.getTrainingName()).isEqualTo("Leg Day"),
                () -> assertThat(result.getTrainingDate()).isEqualTo(trainingDate),
                () -> assertThat(result.getTrainee()).isNull(),
                () -> assertThat(result.getTrainer()).isNull(),
                () -> assertThat(result.getTrainingType()).isNull()
        );
    }

    @Test
    void toEntityForAdditionShouldReturnNullWhenRequestIsNull() {
        assertThat(trainingMapper.toEntityForAddition(null)).isNull();
    }
}
