package com.gymtraining.application.dto;

public record CredentialsResponse(
        String username,
        String password
) {
    @Override
    public String toString() {
        return "CredentialsResponse{" +
                "username=" + (username != null ? "'[PROTECTED]'" : "null") +
                ", password=" + (password != null ? "'[REDACTED]'" : "null") +
                '}';
    }
}
