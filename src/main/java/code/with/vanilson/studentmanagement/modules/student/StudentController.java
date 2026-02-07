package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Students", description = "Endpoints for managing students")
public class StudentController {

    private final StudentService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new student", description = "Creates a new student record. Requires ADMIN role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student created successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(
            @jakarta.validation.Valid @RequestBody StudentDto dto) {
        return ResponseEntity.ok(ApiResponse.success(service.createStudent(dto), "Student created successfully"));
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get student by ID", description = "Retrieves a student by their unique ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student retrieved successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<StudentDto>> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getStudent(id), "Student retrieved successfully"));
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get all students", description = "Retrieves a list of all students.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllStudents(), "Students retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Update student", description = "Updates an existing student record. Requires ADMIN role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student updated successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<StudentDto>> updateStudent(@PathVariable Long id,
            @jakarta.validation.Valid @RequestBody StudentDto dto) {
        return ResponseEntity.ok(ApiResponse.success(service.updateStudent(id, dto), "Student updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Delete student", description = "Deletes a student record by ID. Requires ADMIN role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Student deleted successfully"));
    }
}
