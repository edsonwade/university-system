package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.config.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

        @Autowired
        private MockMvc mockMvc;


        @MockBean
        private AppointmentService appointmentService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @Test
        @DisplayName("POST /api/v1/appointments - Success")
        @WithMockUser
        void scheduleAppointment_Success() throws Exception {
                Appointment appointment = Appointment.builder()
                                .studentId(1L)
                                .teacherId(2L)
                                .startTime(LocalDateTime.of(2024, 12, 31, 10, 0))
                                .endTime(LocalDateTime.of(2024, 12, 31, 11, 0))
                                .status(Appointment.AppointmentStatus.SCHEDULED)
                                .build();

                when(appointmentService.scheduleAppointment(eq(1L), eq(2L), any(LocalDateTime.class),
                                any(LocalDateTime.class)))
                                .thenReturn(appointment);

                mockMvc.perform(post("/api/v1/appointments")
                                .with(csrf())
                                .param("studentId", "1")
                                .param("teacherId", "2")
                                .param("startTime", "2024-12-31T10:00:00")
                                .param("endTime", "2024-12-31T11:00:00"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.studentId").value(1))
                                .andExpect(jsonPath("$.message").value("Appointment scheduled successfully"));
        }

        @Test
        @DisplayName("PUT /api/v1/appointments/{id}/cancel - Success")
        @WithMockUser
        void cancelAppointment_Success() throws Exception {
                Appointment appointment = Appointment.builder()
                                .studentId(1L)
                                .teacherId(2L)
                                .startTime(LocalDateTime.of(2024, 12, 31, 10, 0))
                                .endTime(LocalDateTime.of(2024, 12, 31, 11, 0))
                                .status(Appointment.AppointmentStatus.CANCELLED)
                                .build();

                when(appointmentService.cancelAppointment(1L)).thenReturn(appointment);

                mockMvc.perform(put("/api/v1/appointments/1/cancel")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                                .andExpect(jsonPath("$.message").value("Appointment cancelled successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/appointments/students/{studentId} - Success")
        @WithMockUser
        void getStudentAppointments_Success() throws Exception {
                Appointment appointment = Appointment.builder()
                                .studentId(1L)
                                .teacherId(2L)
                                .startTime(LocalDateTime.of(2024, 12, 31, 10, 0))
                                .endTime(LocalDateTime.of(2024, 12, 31, 11, 0))
                                .status(Appointment.AppointmentStatus.SCHEDULED)
                                .build();
                List<Appointment> appointments = Collections.singletonList(appointment);

                when(appointmentService.getStudentAppointments(1L)).thenReturn(appointments);

                mockMvc.perform(get("/api/v1/appointments/students/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data[0].studentId").value(1))
                                .andExpect(jsonPath("$.message").value("Appointments retrieved successfully"));
        }
}
