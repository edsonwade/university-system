package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.AbstractIntegrationTest;
import code.with.vanilson.studentmanagement.modules.student.StudentDto;
import code.with.vanilson.studentmanagement.modules.student.StudentService;
import code.with.vanilson.studentmanagement.modules.teacher.Teacher;
import code.with.vanilson.studentmanagement.modules.teacher.TeacherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Test
    @DisplayName("Appointment Flow - Schedule and Cancel")
    @WithMockUser(roles = "ADMIN")
    void appointmentFlow_Success() throws Exception {
        // Create student
        StudentDto student = StudentDto.builder()
                .firstName("Appt")
                .lastName("Student")
                .email("appt.student@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();
        student = studentService.createStudent(student);
        Long studentId = student.getId();

        // Create teacher
        Teacher teacher = Teacher.builder()
                .firstName("Appt")
                .lastName("Teacher")
                .email("appt.teacher@example.com")
                .expertise("Math")
                .build();
        teacher = teacherService.createTeacher(teacher);
        Long teacherId = teacher.getId();

        // Schedule Appointment
        String createResponse = mockMvc.perform(post("/api/v1/appointments")
                .param("studentId", studentId.toString())
                .param("teacherId", teacherId.toString())
                .param("startTime", "2024-12-31T10:00:00")
                .param("endTime", "2024-12-31T11:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"))
                .andReturn().getResponse().getContentAsString();

        Integer apptIdInt = com.jayway.jsonpath.JsonPath.read(createResponse, "$.data.id");
        Long apptId = apptIdInt.longValue();

        // Get student appointments
        mockMvc.perform(get("/api/v1/appointments/students/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].studentId").value(studentId));

        // Cancel Appointment
        mockMvc.perform(put("/api/v1/appointments/" + apptId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }
}
