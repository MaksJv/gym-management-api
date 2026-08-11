package com.gymtraining.application.service;

import com.gymtraining.application.exception.DataIntegrityViolationException;
import com.gymtraining.application.exception.TrainingTypeNotFoundException;
import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.repository.TrainerRepository;
import com.gymtraining.application.repository.TrainingRepository;
import com.gymtraining.application.repository.TrainingTypeRepository;
import com.gymtraining.application.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    void saveShouldReturnSavedTrainingTypeWhenSuccessful() {
        TrainingType inputType = new TrainingType(null, "Yoga");
        TrainingType savedType = new TrainingType(1L, "Yoga");
        when(trainingTypeRepository.save(inputType)).thenReturn(savedType);

        TrainingType result = trainingTypeService.save(inputType);

        assertThat(result)
                .as("Check if saved training type is returned correctly")
                .isNotNull()
                .isEqualTo(savedType);
    }

    @Test
    void getAllShouldReturnListOfTrainingTypesWhenCalled() {
        List<TrainingType> mockList = List.of(
                new TrainingType(1L, "Fitness"),
                new TrainingType(2L, "Zumba")
        );
        when(trainingTypeRepository.findAll()).thenReturn(mockList);

        List<TrainingType> result = trainingTypeService.getAll();

        assertAll("Verify list content and size",
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result).containsExactlyInAnyOrderElementsOf(mockList)
        );
    }

    @Test
    void getByIdShouldReturnTrainingTypeWhenIdExists() {
        Long id = 10L;
        TrainingType foundType = new TrainingType(id, "Box");
        when(trainingTypeRepository.findById(id)).thenReturn(Optional.of(foundType));

        TrainingType result = trainingTypeService.getById(id);

        assertAll("Verify found training type properties",
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(id),
                () -> assertThat(result.getName()).isEqualTo("Box")
        );
    }

    @Test
    void getByIdShouldThrowRuntimeExceptionWhenIdDoesNotExist() {
        Long id = 999L;
        when(trainingTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingTypeService.getById(id))
                .isInstanceOf(TrainingTypeNotFoundException.class)
                .hasMessage("Training Type not found");
    }

    @Test
    void deleteTrainingTypeByIdShouldDeleteWhenNoDependenciesExist() {
        Long typeId = 1L;

        when(trainerRepository.existsBySpecializationId(typeId)).thenReturn(false);
        when(trainingRepository.existsByTrainingTypeId(typeId)).thenReturn(false);
        when(trainingTypeRepository.existsById(typeId)).thenReturn(true);

        trainingTypeService.deleteTrainingTypeById(typeId);

        verify(trainingTypeRepository, times(1)).deleteById(typeId);
    }

    @Test
    void deleteTrainingTypeByIdShouldThrowExceptionWhenAssignedToTrainer() {
        Long typeId = 1L;

        when(trainerRepository.existsBySpecializationId(typeId)).thenReturn(true);

        assertThatThrownBy(() -> trainingTypeService.deleteTrainingTypeById(typeId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Cannot delete training type that is assigned to a trainer");

        verifyNoInteractions(trainingRepository, trainingTypeRepository);
    }

    @Test
    void deleteTrainingTypeByIdShouldThrowExceptionWhenLinkedToTrainings() {
        Long typeId = 1L;

        when(trainerRepository.existsBySpecializationId(typeId)).thenReturn(false);
        when(trainingRepository.existsByTrainingTypeId(typeId)).thenReturn(true);

        assertThatThrownBy(() -> trainingTypeService.deleteTrainingTypeById(typeId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Cannot delete training type: linked records exist");

        verify(trainingTypeRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteTrainingTypeByIdShouldThrowExceptionWhenNotFound() {
        Long typeId = 1L;

        when(trainerRepository.existsBySpecializationId(typeId)).thenReturn(false);
        when(trainingRepository.existsByTrainingTypeId(typeId)).thenReturn(false);
        when(trainingTypeRepository.existsById(typeId)).thenReturn(false);

        assertThatThrownBy(() -> trainingTypeService.deleteTrainingTypeById(typeId))
                .isInstanceOf(TrainingTypeNotFoundException.class)
                .hasMessage("Training Type not found with ID: " + typeId);

        verify(trainingTypeRepository, never()).deleteById(anyLong());
    }
}
