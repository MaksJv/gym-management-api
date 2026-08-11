package com.gymtraining.application.service.impl;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.TraineeNotFoundException;
import com.gymtraining.application.exception.TrainerNotFoundException;
import com.gymtraining.application.exception.TrainingNotFoundException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.mapper.TrainingMapper;
import com.gymtraining.application.model.*;
import com.gymtraining.application.repository.TraineeRepository;
import com.gymtraining.application.repository.TrainerRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.TrainingTypeRepository;
import com.gymtraining.application.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;

    @Override
    @Transactional
    public Training createTrainingFromRequest(TrainingCreateRequest request) {
        Trainee trainee = traineeRepository.findById(request.getTraineeId()).orElseThrow(
                () -> new TraineeNotFoundException("Trainee not found!")
        );
        validateEntityActive(trainee, "Trainee");

        Trainer trainer = trainerRepository.findById(request.getTrainerId()).orElseThrow(
                () -> new TrainerNotFoundException("Trainer not found!")
        );
        validateEntityActive(trainer, "Trainer");

        TrainingType trainingType = trainingTypeRepository.findById(request.getTrainingTypeId()).orElseThrow(
                () -> new TrainingTypeNotFoundException("Training Type not found!")
        );
        validateTrainerSpecialization(trainer, trainingType);

        Training training = trainingMapper.toEntity(request);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        return trainingRepository.save(training);
    }

    private void validateEntityActive(User user, String roleName) {
        if (!user.isActive()) {
            throw new IllegalStateException(roleName + " user with id: " + user.getId() + "is not active");
        }
    }

    private void validateTrainerSpecialization(Trainer trainer, TrainingType type) {
        if (!trainer.getSpecialization().equals(type)) {
            throw new IllegalStateException("Trainer does not specialize in " + type.getName());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingResponse getTrainingById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(
                () -> new TrainingNotFoundException("Training not found")
        );

        return trainingMapper.toResponseDto(training);
    }

    @Override
    @Transactional
    public TrainingResponse assignTrainee2Trainer(Long traineeId, Long trainerId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(
                () -> new TraineeNotFoundException("Trainee not found!")
        );
        validateEntityActive(trainee, "Trainee");

        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(
                () -> new TrainerNotFoundException("Trainer not found!")
        );
        validateEntityActive(trainer, "Trainer");

        validateUniqueAssignment(traineeId, trainerId);

        Training training = createInitialTraining(trainee, trainer, trainer.getSpecialization());
        Training savedTraining = trainingRepository.save(training);

        return trainingMapper.toResponseDto(savedTraining);
    }

    private void validateUniqueAssignment(Long traineeId, Long trainerId) {
        boolean exists = trainingRepository.findAllByTrainerId(trainerId).stream()
                .anyMatch(t -> t.getTrainee().getId().equals(traineeId));

        if (exists) {
            throw new IllegalStateException("Trainee with id %d is already assigned to trainer with id %d".formatted(traineeId, trainerId));
        }
    }

    private Training createInitialTraining(Trainee trainee, Trainer trainer, TrainingType trainingType) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Initial Assignment");
        training.setTrainingDate(LocalDateTime.now());
        training.setDurationMinutes(0);
        training.setTrainingType(trainingType);
        return training;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeInfoResponse> fetchTraineesByTrainerId(Long trainerId) {
        return trainingRepository.findActiveTraineesByTrainerId(trainerId).stream()
                .map(trainee -> new TraineeInfoResponse(
                        trainee.getUsername(),
                        trainee.getFirstName(),
                        trainee.getLastName()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerInfoResponse> getTrainersByTraineeId(Long traineeId) {
        return trainingRepository.findActiveTrainersByTraineeId(traineeId).stream()
                .map(trainer -> new TrainerInfoResponse(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization().getName()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainingsByFilter(Long traineeId, Long trainerId) {
        return trainingRepository.findTrainingsByTraineeAndTrainer(traineeId, trainerId).stream()
                .map(trainingMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void addTraining(TrainingAdditionRequest request) {
        Trainee trainee = traineeRepository.findByUsername(request.traineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + request.traineeUsername()));

        Trainer trainer = trainerRepository.findByUsername(request.trainerUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + request.trainerUsername()));

        TrainingType type = trainingTypeRepository.findByNameIgnoreCase(request.trainingType())
                .orElseThrow(() -> new TrainingTypeNotFoundException("Type not found: " + request.trainingType()));

        Training training = trainingMapper.toEntityForAddition(request);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);

        trainingRepository.save(training);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingResponse getAssignmentById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(
                () -> new TrainingNotFoundException("Training assignment not found with id: " + id)
        );

        return trainingMapper.toResponseDto(training);
    }
}
