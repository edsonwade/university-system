package code.with.vanilson.studentmanagement.modules.course;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/degrees")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Degrees", description = "Endpoints for managing degrees")
public class DegreeController {
    private final DegreeService service;

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new degree", description = "Creates a new degree record.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Degree created successfully")
    public ResponseEntity<ApiResponse<Degree>> createDegree(@RequestBody Degree degree) {
        return ResponseEntity.ok(ApiResponse.success(service.createDegree(degree), "Degree created successfully"));
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Get all degrees", description = "Retrieves a list of all degrees.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Degrees retrieved successfully")
    public ResponseEntity<ApiResponse<List<Degree>>> getAllDegrees() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllDegrees(), "Degrees retrieved successfully"));
    }
}
