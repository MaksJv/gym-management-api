package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface TrainerMapper {

    @Mapping(target = "specialization", source = "specialization.name")
    TrainerResponse toResponseDto(Trainer trainer);

    @Mapping(target = "specialization", source = "trainer.specialization.name")
    @Mapping(target = "active", source = "trainer.active")
    @Mapping(target = "trainees", source = "trainees")
    TrainerProfileResponse toProfileResponse(Trainer trainer, List<TraineeInfoResponse> trainees);

    @Mapping(target = "trainingType", source = "training.trainingType.name")
    @Mapping(target = "duration", source = "training.durationMinutes")
    @Mapping(target = "traineeName", source = "traineeFullName")
    TrainerTrainingResponse toTrainerTrainingResponse(Training training, String traineeFullName);

    @Mapping(target = "specialization", source = "trainer.specialization.name")
    @Mapping(target = "active", source = "trainer.active")
    @Mapping(target = "trainees", source = "trainees")
    TrainerUpdateResponse toUpdateResponse(Trainer trainer, List<TraineeInfoResponse> trainees);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    Trainer toTrainer(TrainerCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "specialization", ignore = true)
    void updateTrainerFromRequest(TrainerUpdateRequest request, @MappingTarget Trainer trainer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    Trainer toTrainerFromRegister(TrainerRegistrationRequest request);
}
