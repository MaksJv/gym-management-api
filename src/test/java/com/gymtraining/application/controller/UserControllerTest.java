package com.gymtraining.application.controller;

import com.gymtraining.application.dto.UserResponse;
import com.gymtraining.application.dto.UserUpdateRequest;
import com.gymtraining.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/v1/api/users";

    @Test
    void getUserByIdShouldReturnUserWhenIdExists() throws Exception {
        Long userId = 1L;
        UserResponse expectedUser = UserResponse.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .username("jdoe")
                .active(true)
                .build();
        given(userService.getUserById(userId)).willReturn(expectedUser);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
        assertThat(actual.getId()).isEqualTo(userId);
    }

    @Test
    void deleteUserByIdShouldDoNothingWhenUserExists() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(delete(BASE_URL + "/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllUsersShouldReturnListWhenUsersExist() throws Exception {
        List<UserResponse> expectedList = List.of(
                new UserResponse(1L, "A", "B", "user1", true),
                new UserResponse(2L, "C", "D", "user2", true)
        );
        given(userService.getAllUsers()).willReturn(expectedList);

        MvcResult result = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn();

        List<UserResponse> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(actualList).hasSize(2);
    }

    @Test
    void updateUserShouldReturnUpdatedUserWhenRequestIsValid() throws Exception {
        Long userId = 1L;

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("NewName");
        updateRequest.setLastName("Doe");

        UserResponse updatedUser = UserResponse.builder()
                .id(userId)
                .firstName("NewName")
                .lastName("Doe")
                .username("jdoe")
                .active(true)
                .build();

        given(userService.updateUser(eq(userId), any(UserUpdateRequest.class)))
                .willReturn(updatedUser);

        MvcResult result = mockMvc.perform(put(BASE_URL + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertAll("Verify Updated User",
                () -> assertThat(actual.getFirstName()).isEqualTo("NewName"),
                () -> assertThat(actual.getLastName()).isEqualTo("Doe")
        );
    }
}
