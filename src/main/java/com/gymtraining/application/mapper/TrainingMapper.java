package com.gymtraining.application.mapper;

import com.gymtraining.application.dto.TrainingAdditionRequest;
import com.gymtraining.application.dto.TrainingCreateRequest;
import com.gymtraining.application.dto.TrainingResponse;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TrainingMapper {


    @Mapping(target = "duration", source = "durationMinutes")
    @Mapping(target = "trainingTypeName", source = "trainingType.name")
    @Mapping(target = "traineeName", source = "trainee", qualifiedByName = "mapTrainee")
    @Mapping(target = "trainerName", source = "trainer", qualifiedByName = "mapTrainer")
    TrainingResponse toResponseDto(Training training);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    @Mapping(target = "durationMinutes", source = "duration")
    Training toEntityForAddition(TrainingAdditionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    Training toEntity(TrainingCreateRequest request);

    @Named("mapTrainee")
    default String mapTrainee(Trainee trainee) {
        if (trainee == null) return null;
        return trainee.getFirstName() + " " + trainee.getLastName();
    }

    @Named("mapTrainer")
    default String mapTrainer(Trainer trainer) {
        if (trainer == null) return null;
        return trainer.getFirstName() + " " + trainer.getLastName();
    }
}
