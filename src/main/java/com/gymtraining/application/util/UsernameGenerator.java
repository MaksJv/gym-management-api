package com.gymtraining.application.util;

public interface UsernameGenerator {

    /**
     * Generates a unique username based on the provided first name and last name.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @return A unique username generated from the first and last name.
     */
    String generate(String firstName, String lastName);

}
