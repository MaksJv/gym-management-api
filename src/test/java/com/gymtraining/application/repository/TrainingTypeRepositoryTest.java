package com.gymtraining.application.repository;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainingTypeRepositoryTest {

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
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setName("YOGA");
    }

    @Test
    void saveShouldPersistTrainingTypeAndGenerateId() {
        TrainingType savedType = trainingTypeRepository.save(trainingType);

        assertAll(
                () -> assertThat(savedType.getId()).isNotNull(),
                () -> assertThat(savedType.getName()).isEqualTo("YOGA")
        );
    }

    @Test
    void findByIdShouldReturnTrainingTypeWhenExists() {
        TrainingType savedType = trainingTypeRepository.save(trainingType);

        Optional<TrainingType> foundType = trainingTypeRepository.findById(savedType.getId());

        assertThat(foundType).isPresent();
        assertThat(foundType.get().getName()).isEqualTo("YOGA");
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        Optional<TrainingType> foundType = trainingTypeRepository.findById(999L);

        assertThat(foundType).isEmpty();
    }

    @Test
    void findAllShouldReturnListOfTrainingTypes() {
        trainingTypeRepository.save(trainingType);

        TrainingType secondType = new TrainingType();
        secondType.setName("FITNESS");
        trainingTypeRepository.save(secondType);

        List<TrainingType> result = trainingTypeRepository.findAll();

        assertThat(result).hasSize(2);
    }
}
