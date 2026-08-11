package com.gymtraining.application.service.impl;

import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.repository.TrainerRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.TrainingTypeRepository;
import com.gymtraining.application.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public TrainingType save(TrainingType trainingType) {
        return trainingTypeRepository.save(trainingType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> getAll() {
        return trainingTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingType getById(Long id) {
        return trainingTypeRepository.findById(id).orElseThrow(
                () -> new TrainingTypeNotFoundException("Training Type not found")
        );
    }

    @Override
    @Transactional
    public void deleteTrainingTypeById(Long id) {
        if (trainerRepository.existsBySpecializationId(id)) {
            throw new DataIntegrityViolationException("Cannot delete training type that is assigned to a trainer");
        }

        if (trainingRepository.existsByTrainingTypeId(id)) {
            throw new DataIntegrityViolationException("Cannot delete training type: linked records exist");
        }

        if (!trainingTypeRepository.existsById(id)) {
            throw new TrainingTypeNotFoundException("Training Type not found with ID: " + id);
        }

        trainingTypeRepository.deleteById(id);
    }
}
