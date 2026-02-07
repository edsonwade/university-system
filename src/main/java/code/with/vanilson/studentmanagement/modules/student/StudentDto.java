package code.with.vanilson.studentmanagement.modules.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "First name is required")
    private String firstName;

    @jakarta.validation.constraints.NotBlank(message = "Last name is required")
    private String lastName;

    @jakarta.validation.constraints.NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;

    @jakarta.validation.constraints.NotNull(message = "Date of birth is required")
    @jakarta.validation.constraints.Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @jakarta.validation.constraints.NotBlank(message = "Address is required")
    private String address;

    private String phoneNumber;
}
