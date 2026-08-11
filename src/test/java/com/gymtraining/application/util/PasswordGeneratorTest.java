package com.gymtraining.application.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;
    private static final String TEST_CHARACTERS = "ABCabc123";
    private static final int DEFAULT_LENGTH = 10;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator(TEST_CHARACTERS, DEFAULT_LENGTH);
    }

    @Test
    void generateRandomWithoutArgsShouldReturnPasswordWithDefaultLength() {
        String password = passwordGenerator.generateRandom();

        assertThat(password).hasSize(DEFAULT_LENGTH);
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 10, 16})
    void generateRandomWithLengthShouldReturnPasswordWithCorrectLength(int length) {
        String password = passwordGenerator.generateRandom(length);

        assertThat(password).hasSize(length);
    }

    @Test
    void generateRandomShouldContainOnlyAllowedCharacters() {
        String password = passwordGenerator.generateRandom(100);

        for (char ch : password.toCharArray()) {
            assertThat(TEST_CHARACTERS).contains(String.valueOf(ch));
        }
    }

    @Test
    void generateRandomShouldReturnUniquePasswordsEachTime() {
        String password1 = passwordGenerator.generateRandom(12);
        String password2 = passwordGenerator.generateRandom(12);

        assertThat(password1).isNotEqualTo(password2);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void generateRandomShouldThrowExceptionWhenLengthIsZeroOrNegative(int invalidLength) {
        assertThatThrownBy(() -> passwordGenerator.generateRandom(invalidLength))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password length must be greater than 0");
    }
}
