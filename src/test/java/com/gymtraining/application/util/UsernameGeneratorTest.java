package com.gymtraining.application.util;

import com.gymtraining.application.model.User;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.util.impl.UsernameGeneratorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsernameGeneratorImpl usernameGenerator;

    @Test
    void generateShouldReturnStandardUsernameWhenNoCollisionExists() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        String username = usernameGenerator.generate("John", "Doe");

        assertThat(username).isEqualTo("jdoe");
    }

    @Test
    void generateShouldAppendCounterWhenUsernameCollisionExists() {
        User existingUser1 = new User();
        existingUser1.setUsername("jdoe");

        User existingUser2 = new User();
        existingUser2.setUsername("jdoe1");

        when(userRepository.findAll()).thenReturn(List.of(existingUser1, existingUser2));

        String username = usernameGenerator.generate("John", "Doe");

        assertThat(username).isEqualTo("jdoe2");
    }

    @ParameterizedTest
    @CsvSource({
        "Jöhn, Döé, jdoe",
        "François, Müller, fmuller",
        "René, René, rrene",
        "John-Paul, O'Connor, joconnor"
    })
    void generateShouldTransliterateAndNormalizeSpecialCharacters(String firstName, String lastName, String expected) {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        String username = usernameGenerator.generate(firstName, lastName);

        assertThat(username).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                "null, Smith",
                "'', Smith",
                "'   ', Smith",
                "John, null",
                "John, ''",
                "John, '   '"
            },
            nullValues = "null"
    )
    void generateShouldThrowIllegalArgumentExceptionWhenNamesAreNullOrBlank(String firstName, String lastName) {
        assertThatThrownBy(() -> usernameGenerator.generate(firstName, lastName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name and last name must not be null or empty");
    }

    @Test
    void generateShouldThrowIllegalArgumentExceptionWhenNamesContainNoValidAlphanumericCharacters() {
        assertThatThrownBy(() -> usernameGenerator.generate("!!!", "???"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Names must contain valid alphanumeric characters");
    }
}
