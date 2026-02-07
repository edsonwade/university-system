package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.appointment.AppointmentRepository;
import code.with.vanilson.studentmanagement.modules.billing.InvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        if (cacheManager != null && cacheManager.getCache("students") != null) {
            cacheManager.getCache("students").clear();
        }
        appointmentRepository.deleteAll();
        invoiceRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Create and Get Student")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAndGetStudent() throws Exception {
        // 1. Create Student
        StudentDto dto = StudentDto.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .address("456 Elm St")
                .phoneNumber("9876543210")
                .build();

        mockMvc.perform(post("/api/v1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("alice.smith@example.com"));

        // Verify DB
        assertThat(studentRepository.findAll()).hasSize(1);
        Student savedStudent = studentRepository.findAll().get(0);
        assertThat(savedStudent.getEmail()).isEqualTo("alice.smith@example.com");

        // 2. Get Student
        mockMvc.perform(get("/api/v1/students/" + savedStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Alice"));
    }

    @Test
    @DisplayName("Integration: Update Student")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateStudent() throws Exception {
        // Setup
        Student student = Student.builder()
                .firstName("Bob")
                .lastName("Jones")
                .email("bob.jones@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Old Address")
                .build();
        student = studentRepository.save(student);

        // Update
        StudentDto updateDto = StudentDto.builder()
                .id(student.getId())
                .firstName("Bob")
                .lastName("Jones")
                .email("bob.jones@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("New Address") // Changed
                .phoneNumber("1112223333")
                .build();

        mockMvc.perform(put("/api/v1/students/" + student.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.address").value("New Address"));

        // Verify DB
        Student updatedStudent = studentRepository.findById(student.getId()).orElseThrow();
        assertThat(updatedStudent.getAddress()).isEqualTo("New Address");
    }

    @Test
    @DisplayName("Integration: Delete Student")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteStudent() throws Exception {
        // Setup
        Student student = Student.builder()
                .firstName("Charlie")
                .lastName("Brown")
                .email("charlie.brown@example.com")
                .dateOfBirth(LocalDate.of(2000, 12, 25))
                .address("Peanuts Rd")
                .build();
        student = studentRepository.save(student);

        // Delete
        mockMvc.perform(delete("/api/v1/students/" + student.getId())
                .with(csrf()))
                .andExpect(status().isOk());

        // Verify DB
        assertThat(studentRepository.findById(student.getId())).isEmpty();
    }
}
