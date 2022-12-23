package com.marionete.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marionete.assessment.exceptions.ControllerAdvice;
import com.marionete.assessment.exceptions.TokenException;
import com.marionete.assessment.model.Credentials;
import com.marionete.assessment.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    UserAccountController marioneteController;

    @Mock
    AuthService authService;

    private String validRequestJSON, invalidRequestJSON;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(this.marioneteController)
                .setControllerAdvice(ControllerAdvice.class)
                .build();

        lenient().when(authService.getUserAccountDetails(Mockito.any(Credentials.class)))
                .thenReturn(Mono.empty());
        Credentials credentials = new Credentials();
        credentials.setUsername("Test");
        credentials.setPassword("Test");

        validRequestJSON = new ObjectMapper().writeValueAsString(credentials);

        credentials.setPassword("");

        invalidRequestJSON= new ObjectMapper().writeValueAsString(credentials);

    }

    @Test
    void shouldGetUserAccountDetails() throws Exception {

        mockMvc.perform(post("/marionete/useraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotGetUserAccountDetails_WhenInvalidData() throws Exception {

        mockMvc.perform(post("/marionete/useraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotGetUserAccountDetails_TokenException() throws Exception {

        when(authService.getUserAccountDetails(Mockito.any(Credentials.class)))
                .thenThrow(new TokenException("Test"));

        mockMvc.perform(post("/marionete/useraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotGetUserAccountDetails_AnyException() throws Exception {
        when(authService.getUserAccountDetails(Mockito.any(Credentials.class)))
                .thenThrow(new RuntimeException("Test"));

        mockMvc.perform(post("/marionete/useraccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJSON)
                )
                .andExpect(status().isInternalServerError());
    }
}
