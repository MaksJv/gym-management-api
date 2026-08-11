package com.gymtraining.application.repository;

import com.gymtraining.application.model.User;
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
class UserRepositoryTest {

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
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jdoe");
        user.setPassword("securePass");
        user.setActive(true);
    }

    @Test
    void saveShouldPersistUserAndGenerateId() {
        User savedUser = userRepository.save(user);

        assertAll(
                () -> assertThat(savedUser.getId()).isNotNull(),
                () -> assertThat(savedUser.getUsername()).isEqualTo("jdoe")
        );
    }

    @Test
    void findByIdShouldReturnUserWhenExists() {
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent().contains(savedUser);
    }

    @Test
    void findAllByIsActiveTrueShouldReturnOnlyActiveUsers() {
        userRepository.save(user);

        User inactiveUser = new User();
        inactiveUser.setFirstName("Jane");
        inactiveUser.setLastName("Smith");
        inactiveUser.setUsername("jsmith");
        inactiveUser.setPassword("password");
        inactiveUser.setActive(false);
        userRepository.save(inactiveUser);

        List<User> result = userRepository.findAllByActiveTrue();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByUsernameAndIsActiveTrueShouldReturnActiveUserWhenFound() {
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsernameAndActiveTrue("jdoe");

        assertThat(result).isPresent();
    }

    @Test
    void findByUsernameAndIsActiveTrueShouldReturnEmptyWhenUserIsInactive() {
        user.setActive(false);
        userRepository.save(user);

        Optional<User> result = userRepository.findByUsernameAndActiveTrue("jdoe");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteByIdShouldRemoveUserFromDatabase() {
        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();

        userRepository.deleteById(id);
        Optional<User> foundUser = userRepository.findById(id);

        assertThat(foundUser).isEmpty();
    }
}
