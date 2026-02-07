package code.with.vanilson.studentmanagement.modules.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import code.with.vanilson.studentmanagement.config.JwtUtils;

@WebMvcTest(DegreeController.class)
class DegreeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DegreeService degreeService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /api/v1/degrees - Success")
        @WithMockUser(roles = "ADMIN")
        void createDegree_Success() throws Exception {
                Degree degree = Degree.builder()
                                .name("Bachelor of Science")
                                .department("Computer Science")
                                .durationYears(4)
                                .build();

                when(degreeService.createDegree(any(Degree.class))).thenReturn(degree);

                mockMvc.perform(post("/api/v1/degrees")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(degree)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name").value("Bachelor of Science"))
                                .andExpect(jsonPath("$.message").value("Degree created successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/degrees - Success")
        @WithMockUser
        void getAllDegrees_Success() throws Exception {
                Degree degree = Degree.builder()
                                .name("Bachelor of Science")
                                .department("Computer Science")
                                .durationYears(4)
                                .build();
                List<Degree> degrees = Collections.singletonList(degree);

                when(degreeService.getAllDegrees()).thenReturn(degrees);

                mockMvc.perform(get("/api/v1/degrees"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data[0].name").value("Bachelor of Science"))
                                .andExpect(jsonPath("$.message").value("Degrees retrieved successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/degrees/{id} - Success")
        @WithMockUser
        void getDegreeById_Success() throws Exception {
                Degree degree = Degree.builder()
                                .name("Bachelor of Science")
                                .department("Computer Science")
                                .durationYears(4)
                                .build();

                when(degreeService.getDegreeById(1L)).thenReturn(degree);

                mockMvc.perform(get("/api/v1/degrees/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name").value("Bachelor of Science"))
                                .andExpect(jsonPath("$.message").value("Degree retrieved successfully"));
        }
}
