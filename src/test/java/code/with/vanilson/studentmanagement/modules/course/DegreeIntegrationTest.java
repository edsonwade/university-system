package code.with.vanilson.studentmanagement.modules.course;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DegreeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Degree Flow - Create and Retrieve")
    @WithMockUser(roles = "ADMIN")
    void degreeFlow_Success() throws Exception {
        Degree degree = Degree.builder()
                .name("Integration Degree")
                .department("IT")
                .durationYears(3)
                .build();

        // Create
        mockMvc.perform(post("/api/v1/degrees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(degree)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Integration Degree"));

        // Retrieve all
        mockMvc.perform(get("/api/v1/degrees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[?(@.name == 'Integration Degree')]").exists());
    }
}
