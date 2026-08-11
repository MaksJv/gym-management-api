package com.gymtraining.application.controller;

import com.gymtraining.application.dto.LoginRequest;
import com.gymtraining.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/v1/api/login";

    @Test
    void loginShouldReturnOkStatusWhenCredentialsAreValid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("john.doe")
                .password("securePassword123")
                .build();

        doNothing().when(userService).authenticate(any(LoginRequest.class));

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService, times(1)).authenticate(any(LoginRequest.class));
    }
}
