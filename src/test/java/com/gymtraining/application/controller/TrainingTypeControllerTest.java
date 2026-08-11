package com.gymtraining.application.controller;

import com.gymtraining.application.model.TrainingType;
import com.gymtraining.application.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingTypeController.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    private static final String BASE_URL = "/v1/api/training-types";

    @Test
    void getAllTrainingTypesShouldReturnListWhenTypesExist() throws Exception {
        TrainingType type1 = new TrainingType(1L, "Yoga");
        TrainingType type2 = new TrainingType(2L, "Fitness");
        List<TrainingType> expectedTypes = List.of(type1, type2);

        given(trainingTypeService.getAll()).willReturn(expectedTypes);

        MvcResult result = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<TrainingType> actualTypes = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<TrainingType>>() {}
        );

        assertAll("Verify Training Types List",
                () -> assertThat(actualTypes).hasSize(2),
                () -> assertThat(actualTypes).extracting("name")
                        .containsExactlyInAnyOrder("Yoga", "Fitness"),
                () -> assertThat(actualTypes.get(0).getId()).isEqualTo(1L)
        );
    }

    @Test
    void getAllTrainingTypesShouldReturnEmptyListWhenNoTypesExist() throws Exception {
        given(trainingTypeService.getAll()).willReturn(List.of());

        MvcResult result = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<TrainingType> actualTypes = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<TrainingType>>() {}
        );

        assertThat(actualTypes).isEmpty();
    }
}
