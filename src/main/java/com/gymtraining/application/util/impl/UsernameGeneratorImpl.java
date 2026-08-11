package com.gymtraining.application.util.impl;

import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UsernameGeneratorImpl implements UsernameGenerator {
    private final UserRepository userRepository;

    private static final Pattern ONLY_LATIN_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]");

    @Override
    public String generate(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name must not be null or empty");
        }

        String safeFirstName = transliterate(firstName);
        String safeLastName = transliterate(lastName);

        if (safeFirstName.isEmpty() || safeLastName.isEmpty()) {
            throw new IllegalArgumentException("Names must contain valid alphanumeric characters");
        }

        String baseUsername = (safeFirstName.charAt(0) + safeLastName).toLowerCase();
        String username = baseUsername;
        int counter = 1;

        while (isUsernameExists(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    private String transliterate(String input) {
        if (input == null) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        return ONLY_LATIN_ALPHANUMERIC.matcher(normalized).replaceAll("");
    }

    private boolean isUsernameExists(String username) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }
}
