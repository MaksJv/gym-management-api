package com.gymtraining.application.service.impl;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.EntityAlreadyExistsException;
import com.gymtraining.application.exception.TraineeNotFoundException;
import com.gymtraining.application.mapper.TraineeMapper;
import com.gymtraining.application.model.Trainee;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.repository.TraineeRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.UserRepository;
import com.gymtraining.application.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    private final TraineeMapper traineeMapper;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    @Transactional
    public TraineeResponse createTraineeFromRequest(TraineeCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EntityAlreadyExistsException("Username '" + request.getUsername() + "' is already taken!");
        }

        Trainee trainee = traineeMapper.toTrainee(request);
        trainee.setActive(true);
        trainee.setPassword(request.getPassword());

        Trainee savedTrainee = traineeRepository.save(trainee);

        return traineeMapper.toResponseDto(savedTrainee);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponse getTraineeById(Long id) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));

        return traineeMapper.toResponseDto(trainee);
    }

    @Override
    @Transactional
    public CredentialsResponse registerTrainee(TraineeRegistrationRequest request) {
        Trainee trainee = traineeMapper.toTraineeFromRegister(request);

        String generatedUsername = usernameGenerator.generate(request.firstName(), request.lastName());
        String generatedPassword = passwordGenerator.generateRandom();

        trainee.setUsername(generatedUsername);
        trainee.setPassword(generatedPassword);
        trainee.setActive(true);

        traineeRepository.save(trainee);

        return new CredentialsResponse(generatedUsername, generatedPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeProfileResponse getTraineeProfileByUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee profile not found for user: " + username));

        List<TrainerInfoResponse> trainers = trainingRepository.findAllByTraineeId(trainee.getId())
                .stream()
                .map(Training::getTrainer)
                .distinct()
                .map(trainer -> new TrainerInfoResponse(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization().getName()
                ))
                .toList();

        return traineeMapper.toProfileResponse(trainee, trainers);
    }

    @Override
    @Transactional
    public TraineeUpdateResponse updateTraineeProfile(String username, TraineeUpdateRequest request) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee profile not found for: " + username));

        traineeMapper.updateTraineeFromRequest(request, trainee);

        Trainee updatedTrainee = traineeRepository.save(trainee);

        return traineeMapper.toUpdateResponse(updatedTrainee);
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee profile not found for: " + username));

        trainingRepository.deleteByTraineeId(trainee.getId());

        traineeRepository.delete(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeTrainingResponse> getTraineeTrainings(String username,
                                                             LocalDate from,
                                                             LocalDate to,
                                                             String trainerName,
                                                             String trainingType) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));

        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(23, 59, 59) : null;

        return traineeRepository.findTrainingsByFilters(
                        trainee.getId(), fromDateTime, toDateTime, trainingType, trainerName)
                .stream()
                .map(t -> traineeMapper.toTrainingResponse(t, t.getTrainer().getUsername()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerInfoResponse> getTrainersByTraineeId(Long traineeId) {
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found with id: " + traineeId));

        return trainingRepository.findAllByTraineeId(trainee.getId()).stream()
                .map(Training::getTrainer)
                .distinct()
                .map(trainer -> new TrainerInfoResponse(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization().getName()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteTraineeById(Long traineeId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(
                () -> new TraineeNotFoundException("Trainee not found with id: " + traineeId)
        );

        boolean hasLinkedTrainings = trainingRepository.findAllByTraineeId(trainee.getId()).stream()
                .anyMatch(t -> t.getTrainingDate().isAfter(LocalDateTime.now()));

        if (hasLinkedTrainings) {
            throw new DataIntegrityViolationException("Cannot delete trainee with id %d because they have assigned trainings".formatted(traineeId));
        }

        trainee.setActive(false);
        traineeRepository.save(trainee);
    }
}
