package com.system.application.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify that the protected endpoints actually disable the token.")
    void shouldReturn401WhenAccessingProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Ensures that the Resource Server is functioning.")
    void shouldAllowAccessWithValidJwt() throws Exception {
        mockMvc.perform(get("/api/test")
                        .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should Allow H2Console Without Auth")
    void shouldAllowH2ConsoleWithoutAuth() throws Exception {
        mockMvc.perform(get("/console-h2"))
                .andExpect(status().isOk());
    }
}
