package com.gymtraining.application.service.impl;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.EntityAlreadyExistsException;
import com.gymtraining.application.exception.TrainerNotFoundException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.mapper.TrainerMapper;
import com.gymtraining.application.model.Trainer;
import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.repository.TrainerRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.TrainingTypeRepository;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.service.TrainerService;
import com.gymtraining.application.service.TrainingService;
import com.gymtraining.application.util.PasswordGenerator;
import com.gymtraining.application.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingService trainingService;
    private final TrainerMapper trainerMapper;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    @Transactional
    public TrainerResponse createTrainerFromRequest(TrainerCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EntityAlreadyExistsException("Username '" + request.getUsername() + "' is already taken!");
        }

        TrainingType type = trainingTypeRepository.findById(request.getSpecializationId()).orElseThrow(
                () -> new TrainingTypeNotFoundException("Training type not found")
        );

        Trainer trainer = trainerMapper.toTrainer(request);
        trainer.setActive(true);
        trainer.setPassword(request.getPassword());
        trainer.setSpecialization(type);

        Trainer savedTrainer = trainerRepository.save(trainer);
        return trainerMapper.toResponseDto(savedTrainer);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponse getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));

        return trainerMapper.toResponseDto(trainer);
    }

    @Override
    @Transactional
    public CredentialsResponse registerTrainer(TrainerRegistrationRequest request) {
        TrainingType specialization = trainingTypeRepository.findById(request.specializationId()).orElseThrow (
                () -> new TrainingTypeNotFoundException("Training type not found"));

        Trainer trainer = trainerMapper.toTrainerFromRegister(request);
        trainer.setUsername(usernameGenerator.generate(request.firstName(), request.lastName()));
        trainer.setPassword(passwordGenerator.generateRandom());
        trainer.setSpecialization(specialization);
        trainer.setActive(true);

        trainerRepository.save(trainer);

        return new CredentialsResponse(trainer.getUsername(), trainer.getPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse getTrainerProfileByUsername(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer profile not found"));

        List<TraineeInfoResponse> trainees = trainingService.fetchTraineesByTrainerId(trainer.getId());

        return trainerMapper.toProfileResponse(trainer, trainees);
    }

    @Override
    @Transactional
    public TrainerUpdateResponse updateTrainerProfile(String username, TrainerUpdateRequest request) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer profile not found"));

        trainerMapper.updateTrainerFromRequest(request, trainer);
        Trainer updatedTrainer = trainerRepository.save(trainer);

        List<TraineeInfoResponse> trainees = trainingService.fetchTraineesByTrainerId(updatedTrainer.getId());

        return trainerMapper.toUpdateResponse(updatedTrainer, trainees);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerTrainingResponse> getTrainerTrainings(String username,
                                                             LocalDate from,
                                                             LocalDate to,
                                                             String traineeName) {

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));

        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(23, 59, 59) : null;

        return trainerRepository.findTrainerTrainingsByFilters(
                        trainer.getId(), fromDateTime, toDateTime, traineeName)
                .stream()
                .map(t -> {
                    String traineeFullName = t.getTrainee().getFirstName() + " " + t.getTrainee().getLastName();
                    return trainerMapper.toTrainerTrainingResponse(t, traineeFullName);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeInfoResponse> getTraineesByTrainerId(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new TrainerNotFoundException("Trainer not found with id: " + trainerId);
        }

        return trainingService.fetchTraineesByTrainerId(trainerId);
    }

    @Override
    @Transactional
    public void deleteTrainerById(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(
                () -> new TrainerNotFoundException("Trainer not found for this id: %d".formatted(trainerId)));

        boolean hasLinkedTrainings = trainingRepository.findAllByTrainerId(trainerId).stream()
                .anyMatch(t -> t.getTrainer().getId().equals(trainerId) && t.getTrainingDate().isAfter(LocalDateTime.now()));

        if (hasLinkedTrainings) {
            throw new DataIntegrityViolationException("Cannot delete trainer with id %d because they have assigned trainings".formatted(trainerId));
        }

        trainer.setActive(false);
        trainerRepository.save(trainer);
    }
}
