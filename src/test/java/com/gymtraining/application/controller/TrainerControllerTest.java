package com.gymtraining.application.controller;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.exception.TrainerNotFoundException;
import com.gymtraining.application.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TrainerService trainerService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/v1/api/trainers";

    @Test
    void createTrainerShouldReturnCreatedTrainerWhenRequestIsValid() throws Exception {
        TrainerCreationRequest request = TrainerCreationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("jsmith")
                .password("securePassword12@")
                .specializationId(1L)
                .build();

        TrainerResponse expectedResponse = TrainerResponse.builder()
                .id(10L)
                .firstName("Jane")
                .lastName("Smith")
                .username("jsmith")
                .specialization("Yoga")
                .active(true)
                .build();

        given(trainerService.createTrainerFromRequest(any(TrainerCreationRequest.class)))
                .willReturn(expectedResponse);

        MvcResult result = mvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TrainerResponse actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainerResponse.class);

        assertAll("Verify Successful Trainer Creation",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.getId()).isEqualTo(10L),
                () -> assertThat(actualResponse.getFirstName()).isEqualTo("Jane"),
                () -> assertThat(actualResponse.getSpecialization()).isEqualTo("Yoga"),
                () -> assertThat(actualResponse.isActive()).isTrue()
        );
    }

    @Test
    void createTrainerShouldReturnBadRequestWhenValidationFails() throws Exception {
        TrainerCreationRequest invalidRequest = new TrainerCreationRequest();

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTrainerByIdShouldReturnTrainerWhenTrainerExists() throws Exception {
        Long trainerId = 10L;
        TrainerResponse expectedResponse = TrainerResponse.builder()
                .id(trainerId)
                .firstName("Jane")
                .lastName("Smith")
                .active(true)
                .build();

        given(trainerService.getTrainerById(trainerId)).willReturn(expectedResponse);

        MvcResult result = mvc.perform(get(BASE_URL + "/{id}", trainerId))
                .andExpect(status().isOk())
                .andReturn();

        TrainerResponse actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), TrainerResponse.class);

        assertAll("Verify Trainer Retrieval",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.getId()).isEqualTo(trainerId),
                () -> assertThat(actualResponse.getFirstName()).isEqualTo("Jane")
        );
    }

    @Test
    void getTrainerByIdShouldThrowNotFoundWhenTrainerDoesNotExist() throws Exception {
        Long trainerId = 999L;

        given(trainerService.getTrainerById(trainerId))
                .willThrow(new TrainerNotFoundException("Trainer not found"));

        mvc.perform(get(BASE_URL + "/{id}", trainerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerShouldReturnCreatedStatusAndResponse() throws Exception {
        TrainerRegistrationRequest request = TrainerRegistrationRequest.builder()
                .firstName("Alex")
                .lastName("Smith")
                .specializationId(1L)
                .build();

        CredentialsResponse response = new CredentialsResponse("alex.smith", "securePass123");

        when(trainerService.registerTrainer(any(TrainerRegistrationRequest.class))).thenReturn(response);

        mvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alex.smith"))
                .andExpect(jsonPath("$.password").value("securePass123"));
    }

    @Test
    void getTrainerProfileByUsernameShouldReturnOkAndProfile() throws Exception {
        TrainerProfileResponse response = new TrainerProfileResponse(
                "Alex", "Smith", "Fitness", true, Collections.emptyList()
        );

        when(trainerService.getTrainerProfileByUsername("alex.smith")).thenReturn(response);

        mvc.perform(get(BASE_URL + "/profile/{username}", "alex.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alex"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization").value("Fitness"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateProfileShouldReturnOkAndUpdatedResponse() throws Exception {
        TrainerUpdateRequest request = TrainerUpdateRequest.builder()
                .firstName("Alex")
                .lastName("Johnson")
                .isActive(true)
                .build();

        TrainerUpdateResponse response = TrainerUpdateResponse.builder()
                .username("alex.smith")
                .firstName("Alex")
                .lastName("Johnson")
                .specialization("Bodybuilding")
                .active(true)
                .build();

        when(trainerService.updateTrainerProfile(eq("alex.smith"), any(TrainerUpdateRequest.class))).thenReturn(response);

        mvc.perform(put(BASE_URL + "/{username}", "alex.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alex.smith"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.specialization").value("Bodybuilding"));
    }

    @Test
    void getAssignedTraineesShouldReturnOkAndTraineesList() throws Exception {
        TraineeInfoResponse traineeResponse = TraineeInfoResponse.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .build();
        List<TraineeInfoResponse> responseList = List.of(traineeResponse);

        when(trainerService.getTraineesByTrainerId(1L)).thenReturn(responseList);

        mvc.perform(get(BASE_URL + "/{id}/trainees", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john.doe"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getTrainerTrainingsShouldReturnOkAndTrainingsList() throws Exception {
        TrainerTrainingResponse trainingResponse = TrainerTrainingResponse.builder()
                .trainingName("Powerlifting")
                .trainingDate(LocalDateTime.of(2026, 5, 16, 14, 0))
                .trainingType("Strength")
                .duration(90)
                .traineeName("John Doe")
                .build();
        List<TrainerTrainingResponse> responseList = List.of(trainingResponse);

        when(trainerService.getTrainerTrainings(
                eq("alex.smith"), any(LocalDate.class), any(LocalDate.class), eq("John Doe")
        )).thenReturn(responseList);

        mvc.perform(get(BASE_URL + "/{username}/trainings", "alex.smith")
                        .param("periodFrom", "2026-01-01")
                        .param("periodTo", "2026-12-31")
                        .param("traineeName", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Powerlifting"))
                .andExpect(jsonPath("$[0].duration").value(90))
                .andExpect(jsonPath("$[0].traineeName").value("John Doe"));
    }
}
