package code.with.vanilson.studentmanagement.modules.course;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Courses", description = "Endpoints for managing courses")
public class CourseController {
    private final CourseService service;

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new course", description = "Creates a new course record.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course created successfully")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(ApiResponse.success(service.createCourse(course), "Course created successfully"));
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get all courses", description = "Retrieves a list of all courses.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllCourses(), "Courses retrieved successfully"));
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get course by ID", description = "Retrieves a course by its ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course retrieved successfully")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getCourseById(id), "Course retrieved successfully"));
    }
}
