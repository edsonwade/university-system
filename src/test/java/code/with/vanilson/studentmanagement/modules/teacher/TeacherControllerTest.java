package code.with.vanilson.studentmanagement.modules.teacher;

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

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TeacherService teacherService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /api/v1/teachers - Success")
        @WithMockUser(roles = "ADMIN")
        void createTeacher_Success() throws Exception {
                Teacher teacher = Teacher.builder()
                                .firstName("Jane")
                                .lastName("Smith")
                                .email("jane.smith@example.com")
                                .expertise("Mathematics")
                                .build();

                when(teacherService.createTeacher(any(Teacher.class))).thenReturn(teacher);

                mockMvc.perform(post("/api/v1/teachers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(teacher)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                                .andExpect(jsonPath("$.message").value("Teacher created successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/teachers - Success")
        @WithMockUser
        void getAllTeachers_Success() throws Exception {
                Teacher teacher = Teacher.builder()
                                .firstName("Jane")
                                .lastName("Smith")
                                .email("jane.smith@example.com")
                                .expertise("Mathematics")
                                .build();
                List<Teacher> teachers = Collections.singletonList(teacher);

                when(teacherService.getAllTeachers()).thenReturn(teachers);

                mockMvc.perform(get("/api/v1/teachers"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data[0].firstName").value("Jane"))
                                .andExpect(jsonPath("$.message").value("Teachers retrieved successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/teachers/{id} - Success")
        @WithMockUser
        void getTeacherById_Success() throws Exception {
                Teacher teacher = Teacher.builder()
                                .firstName("Jane")
                                .lastName("Smith")
                                .email("jane.smith@example.com")
                                .expertise("Mathematics")
                                .build();

                when(teacherService.getTeacherById(1L)).thenReturn(teacher);

                mockMvc.perform(get("/api/v1/teachers/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                                .andExpect(jsonPath("$.message").value("Teacher retrieved successfully"));
        }
}
