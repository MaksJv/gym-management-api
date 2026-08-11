package com.gymtraining.application.controller;

import com.gymtraining.application.dto.AssignRequest;
import com.gymtraining.application.dto.TrainingResponse;
import com.gymtraining.application.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainee2trainer")
@RequiredArgsConstructor
public class TrainingAssignmentController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<TrainingResponse> assign(@RequestBody AssignRequest request) {
        TrainingResponse assignment = trainingService.assignTrainee2Trainer(request.getTraineeId(), request.getTrainerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponse> getAssignment(@PathVariable Long id) {
        TrainingResponse assignment = trainingService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }
}
