package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.UserRegistrationRequest;
import com.gymtraining.application.dto.UserRegistrationResponse;
import com.gymtraining.application.dto.UserResponse;
import com.gymtraining.application.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserMapperTest {

    private UserMapper userMapper;
    private User user;
    private UserRegistrationRequest request;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jdoe");
        user.setPassword("secret123");
        user.setActive(true);

        request = new UserRegistrationRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setUsername("jsmith");
        request.setPassword("password321");
    }

    @Test
    void toResponseDtoShouldMapCorrectly() {
        UserResponse response = userMapper.toResponseDto(user);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(user.getId()),
                () -> assertThat(response.getFirstName()).isEqualTo(user.getFirstName()),
                () -> assertThat(response.getLastName()).isEqualTo(user.getLastName()),
                () -> assertThat(response.getUsername()).isEqualTo(user.getUsername()),
                () -> assertThat(response.isActive()).isTrue()
        );
    }

    @Test
    void toResponseShouldMapToRegistrationResponse() {
        UserRegistrationResponse response = userMapper.toResponse(user);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(user.getId()),
                () -> assertThat(response.getFirstName()).isEqualTo(user.getFirstName()),
                () -> assertThat(response.getLastName()).isEqualTo(user.getLastName()),
                () -> assertThat(response.getUsername()).isEqualTo(user.getUsername()),
                () -> assertThat(response.isActive()).isTrue()
        );
    }

    @ParameterizedTest
    @NullSource
    void mappingMethodsShouldReturnNullWhenInputsAreNull(Object nullInput) {
        assertAll(
                () -> assertThat(userMapper.toResponseDto(null)).isNull(),
                () -> assertThat(userMapper.toResponse(null)).isNull()
        );
    }
}
