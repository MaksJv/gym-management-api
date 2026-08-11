package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.UserRegistrationResponse;
import com.gymtraining.application.dto.UserResponse;
import com.gymtraining.application.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "active", source = "active")
    UserResponse toResponseDto(User user);

    @Mapping(target = "active", source = "active")
    UserRegistrationResponse toResponse(User user);
}
