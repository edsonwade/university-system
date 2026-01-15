package code.with.vanilson.studentmanagement.modules.appointment;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for scheduling and managing appointments")
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @Operation(summary = "Schedule appointment", description = "Schedules a new appointment between a student and a teacher.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment scheduled successfully")
    public ResponseEntity<ApiResponse<Appointment>> scheduleAppointment(
            @RequestParam Long studentId,
            @RequestParam Long teacherId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return ResponseEntity.ok(ApiResponse.success(
                service.scheduleAppointment(studentId, teacherId, LocalDateTime.parse(startTime),
                        LocalDateTime.parse(endTime)),
                "Appointment scheduled successfully"));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancels an existing appointment.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment cancelled successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(service.cancelAppointment(id),
                        "Appointment cancelled successfully"));
    }

    @GetMapping("/students/{studentId}")
    @Operation(summary = "Get student appointments", description = "Retrieves all appointments for a specific student.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    public ResponseEntity<ApiResponse<List<Appointment>>> getStudentAppointments(@PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.success(service.getStudentAppointments(studentId),
                        "Appointments retrieved successfully"));
    }
}
