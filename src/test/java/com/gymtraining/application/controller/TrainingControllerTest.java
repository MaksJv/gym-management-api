package com.gymtraining.application.controller;

import com.gymtraining.application.dto.TrainingAdditionRequest;
import com.gymtraining.application.dto.TrainingCreateRequest;
import com.gymtraining.application.dto.TrainingResponse;
import com.gymtraining.application.exception.TrainingNotFoundException;
import com.gymtraining.application.model.Training;
import com.gymtraining.application.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingService trainingService;

    private static final String BASE_URL = "/v1/api/trainings";

    @Test
    void createTrainingShouldReturnCreatedStatusWhenRequestIsValid() throws Exception {
        LocalDateTime trainingTime = LocalDateTime.now().plusDays(1);
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainingName("Power Lifting");
        request.setTrainingDate(trainingTime);
        request.setDurationMinutes(60);
        request.setTraineeId(1L);
        request.setTrainerId(2L);
        request.setTrainingTypeId(3L);

        Training mockCreatedTraining = new Training();
        mockCreatedTraining.setId(500L);
        mockCreatedTraining.setTrainingName("Power Lifting");

        given(trainingService.createTrainingFromRequest(any(TrainingCreateRequest.class)))
                .willReturn(mockCreatedTraining);

        MvcResult result = mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Training actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), Training.class);

        assertAll("Verify Created Training",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.getId()).isEqualTo(500L),
                () -> assertThat(actualResponse.getTrainingName()).isEqualTo("Power Lifting")
        );
    }

    @Test
    void createTrainingShouldReturnBadRequestWhenValidationFails() throws Exception {
        TrainingCreateRequest invalidRequest = new TrainingCreateRequest();
        invalidRequest.setTrainingName("");

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTrainingByIdShouldReturnTrainingWhenIdExists() throws Exception {
        Long trainingId = 500L;
        TrainingResponse responseDto = TrainingResponse.builder()
                .id(trainingId)
                .trainingName("Yoga Flow")
                .duration(45)
                .trainerName("Svetlana")
                .build();

        given(trainingService.getTrainingById(trainingId)).willReturn(responseDto);

        MvcResult result = mvc.perform(get(BASE_URL + "/{id}", trainingId))
                .andExpect(status().isOk())
                .andReturn();

        TrainingResponse actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainingResponse.class);

        assertAll("Verify Training Response DTO",
                () -> assertThat(actual.getId()).isEqualTo(trainingId),
                () -> assertThat(actual.getTrainingName()).isEqualTo("Yoga Flow"),
                () -> assertThat(actual.getTrainerName()).isEqualTo("Svetlana")
        );
    }

    @Test
    void getTrainingByIdShouldReturnNotFoundWhenServiceThrowsException() throws Exception {
        Long trainingId = 999L;
        given(trainingService.getTrainingById(trainingId))
                .willThrow(new TrainingNotFoundException("Training not found"));

        mvc.perform(get(BASE_URL + "/{id}", trainingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTrainingShouldReturnOkStatusWhenRequestIsValid() throws Exception {
        TrainingAdditionRequest request = TrainingAdditionRequest.builder()
                .traineeUsername("john.doe")
                .trainerUsername("alex.smith")
                .trainingName("Cardio Session")
                .trainingDate(LocalDateTime.of(2026, 10, 20, 10, 0))
                .trainingType("Cardio")
                .duration(60)
                .build();

        doNothing().when(trainingService).addTraining(any(TrainingAdditionRequest.class));

        mvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainingService, times(1)).addTraining(any(TrainingAdditionRequest.class));
    }

    @Test
    void getTrainingsShouldReturnOkAndTrainingsListWhenTraineeIdIsProvided() throws Exception {
        TrainingResponse trainingResponse = TrainingResponse.builder()
                .id(1L)
                .trainingName("Cardio Session")
                .trainingDate(LocalDateTime.of(2026, 12, 20, 10, 0))
                .duration(60)
                .trainingTypeName("Cardio")
                .traineeName("John Doe")
                .trainerName("Alex Smith")
                .build();
        List<TrainingResponse> responseList = List.of(trainingResponse);

        when(trainingService.getTrainingsByFilter(1L, null)).thenReturn(responseList);

        mvc.perform(get(BASE_URL)
                        .param("traineeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].trainingName").value("Cardio Session"))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Cardio"))
                .andExpect(jsonPath("$[0].traineeName").value("John Doe"))
                .andExpect(jsonPath("$[0].trainerName").value("Alex Smith"));
    }

    @Test
    void getTrainingsShouldReturnOkAndTrainingsListWhenTrainerIdIsProvided() throws Exception {
        TrainingResponse trainingResponse = TrainingResponse.builder()
                .id(2L)
                .trainingName("Weightlifting")
                .trainingDate(LocalDateTime.of(2026, 6, 21, 14, 0))
                .duration(90)
                .trainingTypeName("Strength")
                .traineeName("John Doe")
                .trainerName("Alex Smith")
                .build();
        List<TrainingResponse> responseList = List.of(trainingResponse);

        when(trainingService.getTrainingsByFilter(null, 2L)).thenReturn(responseList);

        mvc.perform(get(BASE_URL)
                        .param("trainerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].trainingName").value("Weightlifting"))
                .andExpect(jsonPath("$[0].duration").value(90))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Strength"))
                .andExpect(jsonPath("$[0].traineeName").value("John Doe"))
                .andExpect(jsonPath("$[0].trainerName").value("Alex Smith"));
    }

    @Test
    void getTrainingsShouldReturnBadRequestWhenBothIdsAreMissing() throws Exception {
        mvc.perform(get(BASE_URL))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainingService);
    }
}
