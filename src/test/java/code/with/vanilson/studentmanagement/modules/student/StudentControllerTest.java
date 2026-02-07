package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import code.with.vanilson.studentmanagement.config.JwtUtils;
import code.with.vanilson.studentmanagement.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ StudentController.class, SecurityConfig.class })
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentDto createValidStudentDto() {
        return StudentDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/students - Success")
    @WithMockUser(username = "user", roles = "USER")
    void getAllStudents_Success() throws Exception {
        List<StudentDto> students = Collections.singletonList(createValidStudentDto());
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Success")
    @WithMockUser(username = "user", roles = "USER")
    void getStudent_Success() throws Exception {
        StudentDto student = createValidStudentDto();
        when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Not Found")
    @WithMockUser(username = "user", roles = "USER")
    void getStudent_NotFound() throws Exception {
        when(studentService.getStudent(99L)).thenThrow(new ResourceNotFoundException("Student not found"));

        mockMvc.perform(get("/api/v1/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/students - Success (Admin)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createStudent_Success() throws Exception {
        StudentDto dto = createValidStudentDto();
        when(studentService.createStudent(any(StudentDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/students - Forbidden (User)")
    @WithMockUser(username = "user", roles = "USER")
    void createStudent_Forbidden() throws Exception {
        StudentDto dto = createValidStudentDto();

        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/students - Bad Request (Validation)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createStudent_BadRequest() throws Exception {
        StudentDto dto = createValidStudentDto();
        dto.setEmail("invalid-email"); // Invalid email

        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} - Success (Admin)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateStudent_Success() throws Exception {
        StudentDto dto = createValidStudentDto();
        when(studentService.updateStudent(eq(1L), any(StudentDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/students/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Success (Admin)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteStudent_Success() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/v1/students/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }
}
