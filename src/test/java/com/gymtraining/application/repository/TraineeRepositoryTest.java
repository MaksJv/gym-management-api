package com.gymtraining.application.repository;

import com.gymtraining.application.model.Trainee;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TraineeRepositoryTest {

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
    private TraineeRepository traineeRepository;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("john.doe");
        trainee.setPassword("secure_password");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Test Address");
    }

    @Test
    void saveShouldPersistTraineeAndGenerateId() {
        Trainee savedTrainee = traineeRepository.save(trainee);

        assertAll(
                () -> assertThat(savedTrainee.getId()).isNotNull(),
                () -> assertThat(savedTrainee.getUsername()).isEqualTo("john.doe"),
                () -> assertThat(savedTrainee.getAddress()).isEqualTo("Test Address")
        );
    }

    @Test
    void findByIdShouldReturnTraineeWhenExists() {
        Trainee savedTrainee = traineeRepository.save(trainee);

        Optional<Trainee> foundTrainee = traineeRepository.findById(savedTrainee.getId());

        assertThat(foundTrainee).isPresent();
        assertThat(foundTrainee.get().getUsername()).isEqualTo("john.doe");
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        Optional<Trainee> foundTrainee = traineeRepository.findById(999L);

        assertThat(foundTrainee).isEmpty();
    }

    @Test
    void findByUsernameShouldReturnTraineeWhenExists() {
        traineeRepository.save(trainee);

        Optional<Trainee> foundTrainee = traineeRepository.findByUsername("john.doe");

        assertThat(foundTrainee).isPresent();
        assertThat(foundTrainee.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenUsernameNotExists() {
        Optional<Trainee> foundTrainee = traineeRepository.findByUsername("non.existent");

        assertThat(foundTrainee).isEmpty();
    }

    @Test
    void deleteByIdShouldRemoveTraineeFromDatabase() {
        Trainee savedTrainee = traineeRepository.save(trainee);
        Long id = savedTrainee.getId();

        traineeRepository.deleteById(id);
        Optional<Trainee> foundTrainee = traineeRepository.findById(id);

        assertThat(foundTrainee).isEmpty();
    }
}
