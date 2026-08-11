package com.gymtraining.application.controller;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.service.TraineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    @PostMapping
    public ResponseEntity<TraineeResponse> createTrainee(@Valid @RequestBody TraineeCreationRequest request) {
        TraineeResponse createdTrainee = traineeService.createTraineeFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainee);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<TraineeResponse> getTraineeById(@PathVariable Long id) {
        TraineeResponse trainee = traineeService.getTraineeById(id);
        return ResponseEntity.ok(trainee);
    }

    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> register(@Valid @RequestBody TraineeRegistrationRequest request) {
        CredentialsResponse response = traineeService.registerTrainee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.getTraineeProfileByUsername(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateProfile(@PathVariable String username,
                                                                @Valid @RequestBody TraineeUpdateRequest request) {
        return ResponseEntity.ok(traineeService.updateTraineeProfile(username, request));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username) {
        traineeService.deleteTraineeByUsername(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(@PathVariable String username,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                            @RequestParam(required = false) String trainerName,
                                                                            @RequestParam(required = false) String trainingType) {
        List<TraineeTrainingResponse> trainings = traineeService.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/{id}/trainers")
    public ResponseEntity<List<TrainerInfoResponse>> getAssignedTrainers(@PathVariable Long id) {
        List<TrainerInfoResponse> trainers = traineeService.getTrainersByTraineeId(id);
        return ResponseEntity.ok(trainers);
    }
}
