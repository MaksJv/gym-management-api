package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    TraineeResponse toResponseDto(Trainee trainee);

    @Mapping(target = "trainingType", source = "training.trainingType.name")
    @Mapping(target = "duration", source = "training.durationMinutes")
    @Mapping(target = "trainerName", source = "trainerUsername")
    TraineeTrainingResponse toTrainingResponse(Training training, String trainerUsername);

    @Mapping(target = "active", source = "trainee.active")
    @Mapping(target = "trainers", source = "trainers")
    TraineeProfileResponse toProfileResponse(Trainee trainee, List<TrainerInfoResponse> trainers);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "password", ignore = true)
    Trainee toTrainee(TraineeCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", constant = "true")
    Trainee toTraineeFromRegister(TraineeRegistrationRequest request);

    TraineeUpdateResponse toUpdateResponse(Trainee trainee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateTraineeFromRequest(TraineeUpdateRequest request, @MappingTarget Trainee trainee);
}
