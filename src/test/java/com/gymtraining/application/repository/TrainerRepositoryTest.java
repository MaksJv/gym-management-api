package com.gymtraining.application.repository;

import com.gymtraining.application.model.Trainer;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainerRepositoryTest {

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
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setName("Fitness");
        trainingType = trainingTypeRepository.save(trainingType);

        trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("john.doe");
        trainer.setPassword("secure_password");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
    }

    @Test
    void saveShouldPersistTrainerAndGenerateId() {
        Trainer savedTrainer = trainerRepository.save(trainer);

        assertAll(
                () -> assertThat(savedTrainer.getId()).isNotNull(),
                () -> assertThat(savedTrainer.getUsername()).isEqualTo("john.doe"),
                () -> assertThat(savedTrainer.getSpecialization().getName()).isEqualTo("Fitness")
        );
    }

    @Test
    void findByIdShouldReturnTrainerWhenExists() {
        Trainer savedTrainer = trainerRepository.save(trainer);

        Optional<Trainer> foundTrainer = trainerRepository.findById(savedTrainer.getId());

        assertThat(foundTrainer).isPresent();
        assertThat(foundTrainer.get().getUsername()).isEqualTo("john.doe");
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        Optional<Trainer> foundTrainer = trainerRepository.findById(999L);

        assertThat(foundTrainer).isEmpty();
    }

    @Test
    void findByUsernameShouldReturnTrainerWhenExists() {
        trainerRepository.save(trainer);

        Optional<Trainer> foundTrainer = trainerRepository.findByUsername("john.doe");

        assertThat(foundTrainer).isPresent();
        assertThat(foundTrainer.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenUsernameNotExists() {
        Optional<Trainer> foundTrainer = trainerRepository.findByUsername("non.existent");

        assertThat(foundTrainer).isEmpty();
    }

    @Test
    void deleteByIdShouldRemoveTrainerFromDatabase() {
        Trainer savedTrainer = trainerRepository.save(trainer);
        Long id = savedTrainer.getId();

        trainerRepository.deleteById(id);
        Optional<Trainer> foundTrainer = trainerRepository.findById(id);

        assertThat(foundTrainer).isEmpty();
    }
}
