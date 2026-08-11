package com.gymtraining.application.controller;

import com.gymtraining.application.dto.AssignRequest;
import com.gymtraining.application.dto.TrainingResponse;
import com.gymtraining.application.exception.TrainingNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingAssignmentController.class)
class TrainingAssignmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingService trainingService;

    private static final String BASE_URL = "/v1/api/trainee2trainer";

    @Test
    void assignShouldReturnCreatedAssignmentWhenRequestIsValid() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);

        TrainingResponse expectedResponse = TrainingResponse.builder()
                .id(100L)
                .trainingName("Morning Workout")
                .trainingDate(LocalDateTime.now())
                .duration(60)
                .traineeName("John Doe")
                .trainerName("Jane Smith")
                .build();

        given(trainingService.assignTrainee2Trainer(1L, 2L)).willReturn(expectedResponse);

        MvcResult result = mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TrainingResponse actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainingResponse.class);

        assertAll("Verify Assign Response",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.getId()).isEqualTo(100L),
                () -> assertThat(actualResponse.getTrainingName()).isEqualTo("Morning Workout"),
                () -> assertThat(actualResponse.getTraineeName()).isEqualTo("John Doe"),
                () -> assertThat(actualResponse.getTrainerName()).isEqualTo("Jane Smith")
        );
    }

    @Test
    void getAssignmentShouldReturnAssignmentWhenIdExists() throws Exception {
        Long assignmentId = 100L;
        TrainingResponse expectedResponse = TrainingResponse.builder()
                .id(assignmentId)
                .trainingName("Cardio Session")
                .build();

        given(trainingService.getAssignmentById(assignmentId)).willReturn(expectedResponse);

        MvcResult result = mvc.perform(get(BASE_URL + "/{id}", assignmentId))
                .andExpect(status().isOk())
                .andReturn();

        TrainingResponse actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainingResponse.class);

        assertAll("Verify Get Assignment Response",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.getId()).isEqualTo(assignmentId),
                () -> assertThat(actualResponse.getTrainingName()).isEqualTo("Cardio Session")
        );
    }

    @Test
    void getAssignmentShouldThrowNotFoundWhenIdDoesNotExist() throws Exception {
        Long assignmentId = 999L;
        given(trainingService.getAssignmentById(assignmentId))
                .willThrow(new TrainingNotFoundException("Assignment not found"));

        mvc.perform(get(BASE_URL + "/{id}", assignmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignShouldThrowBadRequestWhenServiceFails() throws Exception {
        AssignRequest request = new AssignRequest();
        request.setTraineeId(1L);
        request.setTrainerId(2L);

        given(trainingService.assignTrainee2Trainer(anyLong(), anyLong()))
                .willThrow(new IllegalArgumentException("Invalid assignment"));

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
