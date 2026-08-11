package com.gymtraining.application.controller;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody TrainerCreationRequest request) {
        TrainerResponse createdTrainer = trainerService.createTrainerFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponse> getTrainerById(@PathVariable Long id) {
        TrainerResponse trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(trainer);
    }

    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> register(@Valid @RequestBody TrainerRegistrationRequest request) {
        CredentialsResponse response = trainerService.registerTrainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfileByUsername(@PathVariable String username) {
        TrainerProfileResponse profile = trainerService.getTrainerProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateProfile(@PathVariable String username,
                                                               @Valid @RequestBody TrainerUpdateRequest request) {
        TrainerUpdateResponse updatedProfile = trainerService.updateTrainerProfile(username, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/{id}/trainees")
    public ResponseEntity<List<TraineeInfoResponse>> getAssignedTrainees(@PathVariable Long id) {
        List<TraineeInfoResponse> trainees = trainerService.getTraineesByTrainerId(id);
        return ResponseEntity.ok(trainees);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName) {

        List<TrainerTrainingResponse> trainings = trainerService.getTrainerTrainings(username, periodFrom, periodTo, traineeName);
        return ResponseEntity.ok(trainings);
    }
}
