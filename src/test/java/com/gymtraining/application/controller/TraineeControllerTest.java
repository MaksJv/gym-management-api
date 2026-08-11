package com.gymtraining.application.controller;

import com.gymtraining.application.dto.*;
import com.gymtraining.application.service.TraineeService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TraineeService traineeService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/v1/api/trainees";

    @Test
    void createTraineeShouldReturnCreatedTraineeWhenRequestIsValid() throws Exception {
        TraineeCreationRequest request = new TraineeCreationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("jdoe");
        request.setPassword("Password123@");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("Main St 123");

        TraineeResponse expectedResponse = TraineeResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("jdoe")
                .active(true)
                .build();

        given(traineeService.createTraineeFromRequest(any(TraineeCreationRequest.class)))
                .willReturn(expectedResponse);

        MvcResult result = mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TraineeResponse actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), TraineeResponse.class);

        assertAll("Verify Successful Trainee Creation",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.getId()).isEqualTo(1L),
                () -> assertThat(actualResponse.getFirstName()).isEqualTo("John"),
                () -> assertThat(actualResponse.isActive()).isTrue()
        );
    }

    @Test
    void createTraineeShouldReturnBadRequestWhenValidationFails() throws Exception {
        TraineeCreationRequest invalidRequest = new TraineeCreationRequest();

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTraineeByIdShouldReturnTraineeWhenTraineeExists() throws Exception {
        Long traineeId = 1L;
        TraineeResponse expectedResponse = TraineeResponse.builder()
                .id(traineeId)
                .firstName("John")
                .lastName("Doe")
                .build();

        given(traineeService.getTraineeById(traineeId)).willReturn(expectedResponse);

        MvcResult result = mvc.perform(get(BASE_URL + "/{id}", traineeId))
                .andExpect(status().isOk())
                .andReturn();

        TraineeResponse actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), TraineeResponse.class);

        assertAll("Verify Trainee Retrieval",
                () -> assertThat(actualResponse.getId()).isEqualTo(traineeId),
                () -> assertThat(actualResponse.getFirstName()).isEqualTo("John"),
                () -> assertThat(actualResponse.getLastName()).isEqualTo("Doe")
        );
    }

    @Test
    void registerShouldReturnCreatedStatusAndResponse() throws Exception {
        TraineeRegistrationRequest request = TraineeRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Main Street 123")
                .build();

        CredentialsResponse response = new CredentialsResponse("john.doe", "password123");

        when(traineeService.registerTrainee(any(TraineeRegistrationRequest.class))).thenReturn(response);

        mvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("password123"));
    }

    @Test
    void getTraineeProfileByUsernameShouldReturnOkAndProfile() throws Exception {
        TraineeProfileResponse response = new TraineeProfileResponse(
                "John", "Doe", LocalDate.of(2000, 1, 1), "Main Street 123", true, Collections.emptyList()
        );

        when(traineeService.getTraineeProfileByUsername("john.doe")).thenReturn(response);

        mvc.perform(get(BASE_URL + "/profile/{username}", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateProfileShouldReturnOkAndUpdatedResponse() throws Exception {
        TraineeUpdateRequest request = TraineeUpdateRequest.builder()
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New Address 456")
                .active(true)
                .build();

        TraineeUpdateResponse response = TraineeUpdateResponse.builder()
                .username("johndoe")
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New Address 456")
                .active(true)
                .build();

        when(traineeService.updateTraineeProfile(eq("johndoe"), any(TraineeUpdateRequest.class))).thenReturn(response);

        mvc.perform(put(BASE_URL + "/{username}", "johndoe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void deleteTraineeShouldReturnOk() throws Exception {
        doNothing().when(traineeService).deleteTraineeByUsername("john.doe");

        mvc.perform(delete(BASE_URL + "/{username}", "john.doe"))
                .andExpect(status().isOk());
    }

    @Test
    void getTraineeTrainingsShouldReturnOkAndTrainingsList() throws Exception {
        TraineeTrainingResponse trainingResponse = TraineeTrainingResponse.builder()
                .trainingName("Cardio Session")
                .trainingDate(LocalDateTime.of(2026, 5, 16, 10, 0))
                .trainingType("Cardio")
                .duration(60)
                .trainerName("Alex Smith")
                .build();

        List<TraineeTrainingResponse> responseList = List.of(trainingResponse);

        when(traineeService.getTraineeTrainings(
                eq("john.doe"), any(LocalDate.class), any(LocalDate.class), eq("Alex Smith"), eq("Cardio")
        )).thenReturn(responseList);

        mvc.perform(get(BASE_URL + "/{username}/trainings", "john.doe")
                        .param("periodFrom", "2026-01-01")
                        .param("periodTo", "2026-12-31")
                        .param("trainerName", "Alex Smith")
                        .param("trainingType", "Cardio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Cardio Session"))
                .andExpect(jsonPath("$[0].duration").value(60))
                .andExpect(jsonPath("$[0].trainerName").value("Alex Smith"));
    }

    @Test
    void getAssignedTrainersShouldReturnOkAndTrainersList() throws Exception {
        TrainerInfoResponse trainerResponse = TrainerInfoResponse.builder()
                .username("alex.smith")
                .firstName("Alex")
                .lastName("Smith")
                .specialization("Fitness")
                .build();

        List<TrainerInfoResponse> responseList = List.of(trainerResponse);

        when(traineeService.getTrainersByTraineeId(1L)).thenReturn(responseList);

        mvc.perform(get(BASE_URL + "/{id}/trainers", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alex.smith"))
                .andExpect(jsonPath("$[0].specialization").value("Fitness"))
                .andExpect(jsonPath("$[0].firstName").value("Alex"));
    }
}
