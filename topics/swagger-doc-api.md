# Implementing Swagger Documentation in REST API

Swagger is a popular tool for documenting RESTful APIs. It provides a user-friendly interface to explore and interact
with API endpoints, as well as generating API documentation automatically from source code annotations.

## Overview

Swagger simplifies API documentation by generating interactive documentation directly from the API source code. It
allows developers to visualize and understand the APIs capabilities without diving into code.

## Steps to Implement Swagger Documentation

### 1. Include Dependencies

Ensure your project includes the necessary dependencies for Swagger. For Spring Boot, add the following dependencies:

```xml

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

1. Swagger Configuration
   Create a configuration class to enable Swagger and customize settings:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.controllers"))
                .paths(PathSelectors.any())
                .build();
    }
}
```

2. Adding Swagger Annotations
   Use Swagger annotations (@Api, @ApiOperation, @ApiParam) in your controller classes to provide additional metadata:

```java
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/students")
@Api(tags = "Students", description = "Operations related to students")
public class StudentController {

    @GetMapping("/{id}")
    @ApiOperation(value = "Get student by ID", notes = "Provide an ID to look up specific student details")
    public ResponseEntity<Student> getStudentById(
            @ApiParam(value = "ID of the student", required = true) @PathVariable Long id) {
        // Implementation code to get student by ID
    }

    @PostMapping("/")
    @ApiOperation(value = "Create a new student", response = Student.class)
    public ResponseEntity<Student> createStudent(
            @ApiParam(value = "Student object to be created", required = true) @RequestBody Student student) {
        // Implementation code to create a new student
    }
}
```

4. Accessing Swagger UI
   Run your Spring Boot application and access Swagger UI at [Swagger UI](http://localhost:8080/swagger-ui/index.html)
   Here, you can explore API endpoints, test requests, and view auto-generated documentation.
5. Customization and Security
   Customize Swagger UI appearance and behavior using additional configurations in SwaggerConfig. Implement security
   configurations for production environments using Spring Security.

## Benefits of Swagger

- **Interactive Documentation**: Provides an interactive UI for exploring API endpoints.
- **Auto-generated Documentation**: Generates API documentation from code annotations.
- **Client Code Generation**: Allows generating client SDKs in various programming languages.