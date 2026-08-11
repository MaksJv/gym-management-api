package com.gymtraining.application.controller;

import com.gymtraining.application.dto.TrainingAdditionRequest;
import com.gymtraining.application.dto.TrainingCreateRequest;
import com.gymtraining.application.dto.TrainingResponse;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Training> createTrainingFromRequest(@Valid @RequestBody TrainingCreateRequest request) {
        Training createdTraining = trainingService.createTrainingFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTraining);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponse> getTrainingById(@PathVariable Long id) {
        return ResponseEntity.ok(trainingService.getTrainingById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingAdditionRequest request) {
        trainingService.addTraining(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TrainingResponse>> getTrainings(
            @RequestParam(required = false) Long traineeId,
            @RequestParam(required = false) Long trainerId) {

        if (traineeId == null && trainerId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<TrainingResponse> trainings = trainingService.getTrainingsByFilter(traineeId, trainerId);
        return ResponseEntity.ok(trainings);
    }
}
