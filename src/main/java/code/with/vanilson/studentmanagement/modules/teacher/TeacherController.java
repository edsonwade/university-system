package code.with.vanilson.studentmanagement.modules.teacher;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Teachers", description = "Endpoints for managing teachers")
public class TeacherController {
    private final TeacherService service;

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new teacher", description = "Creates a new teacher record.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Teacher created successfully")
    public ResponseEntity<ApiResponse<Teacher>> createTeacher(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(ApiResponse.success(service.createTeacher(teacher), "Teacher created successfully"));
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get all teachers", description = "Retrieves a list of all teachers.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Teachers retrieved successfully")
    public ResponseEntity<ApiResponse<List<Teacher>>> getAllTeachers() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllTeachers(), "Teachers retrieved successfully"));
    }
}
