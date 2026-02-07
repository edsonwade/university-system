package code.with.vanilson.studentmanagement.modules.auth;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Full Auth Flow - Register and Authenticate")
    void authFlow_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Integration")
                .lastname("Test")
                .email("integration.test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        // Register
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists());

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("integration.test@example.com")
                .password("password123")
                .build();

        // Authenticate
        mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }
}
