package com.gymtraining.application.repository;

import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainingRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private Training training;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setFirstName("Alex");
        trainee.setLastName("Smith");
        trainee.setUsername("alex.smith");
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(1995, 5, 10));
        trainee = traineeRepository.save(trainee);

        trainingType = new TrainingType();
        trainingType.setName("Cardio");
        trainingType = trainingTypeRepository.save(trainingType);

        trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("john.doe");
        trainer.setPassword("password");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer = trainerRepository.save(trainer);

        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Run");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.now().plusDays(1));
        training.setDurationMinutes(45);
    }

    @Test
    void saveShouldPersistTrainingAndGenerateId() {
        Training savedTraining = trainingRepository.save(training);

        assertAll(
                () -> assertThat(savedTraining.getId()).isNotNull(),
                () -> assertThat(savedTraining.getTrainingName()).isEqualTo("Morning Run"),
                () -> assertThat(savedTraining.getTrainee().getId()).isEqualTo(trainee.getId()),
                () -> assertThat(savedTraining.getTrainer().getId()).isEqualTo(trainer.getId())
        );
    }

    @Test
    void findByIdShouldReturnTrainingWhenExists() {
        Training savedTraining = trainingRepository.save(training);

        Optional<Training> foundTraining = trainingRepository.findById(savedTraining.getId());

        assertThat(foundTraining).isPresent();
        assertThat(foundTraining.get().getTrainingName()).isEqualTo("Morning Run");
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        Optional<Training> foundTraining = trainingRepository.findById(999L);

        assertThat(foundTraining).isEmpty();
    }

    @Test
    void findAllByTraineeIdShouldReturnTrainingsList() {
        trainingRepository.save(training);

        List<Training> trainings = trainingRepository.findAllByTraineeId(trainee.getId());

        assertThat(trainings).isNotEmpty().hasSize(1);
        assertThat(trainings.get(0).getTrainingName()).isEqualTo("Morning Run");
    }

    @Test
    void findAllByTrainerIdShouldReturnTrainingsList() {
        trainingRepository.save(training);

        List<Training> trainings = trainingRepository.findAllByTrainerId(trainer.getId());

        assertThat(trainings).isNotEmpty().hasSize(1);
        assertThat(trainings.get(0).getTrainingName()).isEqualTo("Morning Run");
    }
}
