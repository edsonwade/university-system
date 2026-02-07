package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private List<Student> testStudents;

    @BeforeEach
    void setUp() {
        testStudent = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();

      var  testStudentDto = StudentDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();

        testStudents = Arrays.asList(
                testStudent,
                Student.builder()
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("jane.smith@example.com")
                        .dateOfBirth(LocalDate.of(1999, 5, 15))
                        .address("456 Oak Ave")
                        .phoneNumber("9876543210")
                        .build()
        );
    }

    @Test
    @DisplayName("Should create a new student successfully")
    void createStudent_Success() {
        // Given
        StudentDto newStudentDto = StudentDto.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .dateOfBirth(LocalDate.of(2001, 3, 10))
                .address("789 Pine Rd")
                .phoneNumber("5551234567")
                .build();

        Student savedStudent = Student.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .dateOfBirth(LocalDate.of(2001, 3, 10))
                .address("789 Pine Rd")
                .phoneNumber("5551234567")
                .build();

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // When
        StudentDto result = studentService.createStudent(newStudentDto);

        // Then
        assertNotNull(result);
        assertEquals(savedStudent.getId(), result.getId());
        assertEquals(savedStudent.getFirstName(), result.getFirstName());
        assertEquals(savedStudent.getLastName(), result.getLastName());
        assertEquals(savedStudent.getEmail(), result.getEmail());
        assertEquals(savedStudent.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(savedStudent.getAddress(), result.getAddress());
        assertEquals(savedStudent.getPhoneNumber(), result.getPhoneNumber());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should get student by ID successfully")
    void getStudent_Success() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));

        // When
        StudentDto result = studentService.getStudent(studentId);

        // Then
        assertNotNull(result);
        assertEquals(testStudent.getId(), result.getId());
        assertEquals(testStudent.getFirstName(), result.getFirstName());
        assertEquals(testStudent.getLastName(), result.getLastName());
        assertEquals(testStudent.getEmail(), result.getEmail());
        assertEquals(testStudent.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(testStudent.getAddress(), result.getAddress());
        assertEquals(testStudent.getPhoneNumber(), result.getPhoneNumber());
        verify(studentRepository).findById(studentId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student not found by ID")
    void getStudent_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getStudent(nonExistentId)
        );
        assertEquals("student.not_found", exception.getMessage());
        verify(studentRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get all students successfully")
    void getAllStudents_Success() {
        // Given
        when(studentRepository.findAll()).thenReturn(testStudents);

        // When
        List<StudentDto> result = studentService.getAllStudents();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testStudents.get(0).getFirstName(), result.get(0).getFirstName());
        assertEquals(testStudents.get(1).getFirstName(), result.get(1).getFirstName());
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no students exist")
    void getAllStudents_EmptyList() {
        // Given
        when(studentRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<StudentDto> result = studentService.getAllStudents();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should update student successfully")
    void updateStudent_Success() {
        // Given
        Long studentId = 1L;
        StudentDto updateDto = StudentDto.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Updated St")
                .phoneNumber("1111111111")
                .build();

        Student updatedStudent = Student.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Updated St")
                .phoneNumber("1111111111")
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // When
        StudentDto result = studentService.updateStudent(studentId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(updatedStudent.getFirstName(), result.getFirstName());
        assertEquals(updatedStudent.getLastName(), result.getLastName());
        assertEquals(updatedStudent.getEmail(), result.getEmail());
        assertEquals(updatedStudent.getAddress(), result.getAddress());
        assertEquals(updatedStudent.getPhoneNumber(), result.getPhoneNumber());
        // Date of birth should remain unchanged as it\'s not updated in the service
        assertEquals(testStudent.getDateOfBirth(), result.getDateOfBirth());
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent student")
    void updateStudent_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        StudentDto updateDto = StudentDto.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();

        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.updateStudent(nonExistentId, updateDto)
        );
        assertEquals("student.not_found", exception.getMessage());
        verify(studentRepository).findById(nonExistentId);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void deleteStudent_Success() {
        // Given
        Long studentId = 1L;
        doNothing().when(studentRepository).deleteById(studentId);

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    @DisplayName("Should handle repository exception during student creation")
    void createStudent_RepositoryException_ThrowsException() {
        // Given
        StudentDto newStudentDto = StudentDto.builder()
                .firstName("Error")
                .lastName("Student")
                .email("error@example.com")
                .build();

        when(studentRepository.save(any(Student.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> studentService.createStudent(newStudentDto)
        );
        assertEquals("Database error", exception.getMessage());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should handle repository exception during student update")
    void updateStudent_RepositoryException_ThrowsException() {
        // Given
        Long studentId = 1L;
        StudentDto updateDto = StudentDto.builder()
                .firstName("Error")
                .lastName("Update")
                .email("error@example.com")
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> studentService.updateStudent(studentId, updateDto)
        );
        assertEquals("Database error", exception.getMessage());
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should handle repository exception during getAllStudents")
    void getAllStudents_RepositoryException_ThrowsException() {
        // Given
        when(studentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> studentService.getAllStudents()
        );
        assertEquals("Database error", exception.getMessage());
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should create student with null optional fields")
    void createStudent_WithNullOptionalFields_Success() {
        // Given
        StudentDto studentWithNulls = StudentDto.builder()
                .firstName("Test")
                .lastName("Student")
                .email("test@example.com")
                .dateOfBirth(null)
                .address(null)
                .phoneNumber(null)
                .build();

        Student savedStudent = Student.builder()
                .firstName("Test")
                .lastName("Student")
                .email("test@example.com")
                .dateOfBirth(null)
                .address(null)
                .phoneNumber(null)
                .build();

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // When
        StudentDto result = studentService.createStudent(studentWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(savedStudent.getId(), result.getId());
        assertEquals(savedStudent.getFirstName(), result.getFirstName());
        assertEquals(savedStudent.getLastName(), result.getLastName());
        assertEquals(savedStudent.getEmail(), result.getEmail());
        assertNull(result.getDateOfBirth());
        assertNull(result.getAddress());
        assertNull(result.getPhoneNumber());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should update student with partial data")
    void updateStudent_WithPartialData_Success() {
        // Given
        Long studentId = 1L;
        StudentDto partialUpdateDto = StudentDto.builder()
                .firstName("Partial Update")
                .build(); // Only updating first name

        Student updatedStudent = Student.builder()
                .firstName("Partial Update")
                .lastName("Doe") // Should remain unchanged
                .email("john.doe@example.com") // Should remain unchanged
                .dateOfBirth(LocalDate.of(2000, 1, 1)) // Should remain unchanged
                .address("123 Main St") // Should remain unchanged
                .phoneNumber("1234567890") // Should remain unchanged
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // When
        StudentDto result = studentService.updateStudent(studentId, partialUpdateDto);

        // Then
        assertNotNull(result);
        assertEquals("Partial Update", result.getFirstName());
        assertEquals("Doe", result.getLastName()); // Should remain unchanged
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }
}
