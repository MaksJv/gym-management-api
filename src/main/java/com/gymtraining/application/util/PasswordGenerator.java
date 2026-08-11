package com.gymtraining.application.util;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Component
public class PasswordGenerator {

    private final SecureRandom random = new SecureRandom();

    private final String characters;
    private final int defaultLength;

    public PasswordGenerator(
            @Value("${app.security.password.characters}") String characters,
            @Value("${app.security.password.default-length}") int defaultLength) {
        this.characters = characters;
        this.defaultLength = defaultLength;
    }

    public @NotNull String generateRandom() {
        return generateRandom(defaultLength);
    }

    public @NotNull String generateRandom(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than 0");
        }

        return random.ints(length, 0, characters.length())
                .mapToObj(characters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
