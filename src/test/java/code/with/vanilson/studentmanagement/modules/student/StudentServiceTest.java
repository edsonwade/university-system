package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService service;

    @Test
    @DisplayName("Create Student - Should return StudentDto when data is valid")
    void createStudent_ShouldReturnStudentDto_WhenDataIsValid() {
        // Arrange
        StudentDto dto = StudentDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();

        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();
        student.setId(1L);

        when(repository.save(any(Student.class))).thenReturn(student);

        // Act
        StudentDto result = service.createStudent(dto);

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getId());
        assertEquals(student.getFirstName(), result.getFirstName());
        assertEquals(student.getEmail(), result.getEmail());
        verify(repository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Get Student - Should return StudentDto when student exists")
    void getStudent_ShouldReturnStudentDto_WhenStudentExists() {
        // Arrange
        Long studentId = 1L;
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        student.setId(studentId);

        when(repository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        StudentDto result = service.getStudent(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(studentId, result.getId());
        assertEquals(student.getEmail(), result.getEmail());
        verify(repository, times(1)).findById(studentId);
    }

    @Test
    @DisplayName("Get Student - Should throw ResourceNotFoundException when student does not exist")
    void getStudent_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {
        // Arrange
        Long studentId = 1L;
        when(repository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.getStudent(studentId);
        });

        assertEquals("student.not_found", exception.getMessage());
        assertArrayEquals(new Object[] { studentId }, exception.getArgs());
        verify(repository, times(1)).findById(studentId);
    }

    @Test
    @DisplayName("Get All Students - Should return list of StudentDtos")
    void getAllStudents_ShouldReturnListOfStudentDtos() {
        // Arrange
        Student student1 = Student.builder().firstName("John").build();
        student1.setId(1L);
        Student student2 = Student.builder().firstName("Jane").build();
        student2.setId(2L);
        when(repository.findAll()).thenReturn(List.of(student1, student2));

        // Act
        List<StudentDto> result = service.getAllStudents();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update Student - Should return updated StudentDto when student exists")
    void updateStudent_ShouldReturnUpdatedStudentDto_WhenStudentExists() {
        // Arrange
        Long studentId = 1L;
        StudentDto updateDto = StudentDto.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .address("456 New St")
                .phoneNumber("0987654321")
                .build();

        Student existingStudent = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        existingStudent.setId(studentId);

        Student updatedStudent = Student.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .address("456 New St")
                .phoneNumber("0987654321")
                .build();
        updatedStudent.setId(studentId);

        when(repository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(repository.save(any(Student.class))).thenReturn(updatedStudent);

        // Act
        StudentDto result = service.updateStudent(studentId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("John Updated", result.getFirstName());
        assertEquals("Doe Updated", result.getLastName());
        verify(repository, times(1)).findById(studentId);
        verify(repository, times(1)).save(existingStudent);
    }

    @Test
    @DisplayName("Update Student - Should throw ResourceNotFoundException when student does not exist")
    void updateStudent_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {
        // Arrange
        Long studentId = 1L;
        StudentDto updateDto = StudentDto.builder().build();
        when(repository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.updateStudent(studentId, updateDto);
        });

        assertEquals("student.not_found", exception.getMessage());
        assertArrayEquals(new Object[] { studentId }, exception.getArgs());
        verify(repository, times(1)).findById(studentId);
        verify(repository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Delete Student - Should call repository delete")
    void deleteStudent_ShouldCallRepositoryDelete() {
        // Arrange
        Long studentId = 1L;

        // Act
        service.deleteStudent(studentId);

        // Assert
        verify(repository, times(1)).deleteById(studentId);
    }
}
