package code.with.vanilson.studentmanagement.modules.teacher;



import code.with.vanilson.studentmanagement.common.exception.ResourceAlreadyExistsException;
import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService Unit Tests")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;
    private List<Teacher> testTeachers;

    @BeforeEach
    void setUp() {
        testTeacher = Teacher.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .expertise("Computer Science")
                .build();

        testTeachers = Arrays.asList(
                testTeacher,
                Teacher.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .email("jane.doe@example.com")
                        .expertise("Mathematics")
                        .build()
        );
    }

    @Test
    @DisplayName("Should create a new teacher successfully")
    void createTeacher_Success() {
        // Given
        Teacher newTeacher = Teacher.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .expertise("Physics")
                .build();

        Teacher savedTeacher = Teacher.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .expertise("Physics")
                .build();

        when(teacherRepository.existsByEmail(newTeacher.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // When
        Teacher result = teacherService.createTeacher(newTeacher);

        // Then
        assertNotNull(result);
        assertEquals(savedTeacher.getId(), result.getId());
        assertEquals(savedTeacher.getFirstName(), result.getFirstName());
        assertEquals(savedTeacher.getLastName(), result.getLastName());
        assertEquals(savedTeacher.getEmail(), result.getEmail());
        assertEquals(savedTeacher.getExpertise(), result.getExpertise());
        verify(teacherRepository).existsByEmail(newTeacher.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExistsException when email already exists")
    void createTeacher_EmailAlreadyExists_ThrowsException() {
        // Given
        Teacher newTeacher = Teacher.builder()
                .firstName("Duplicate")
                .lastName("Email")
                .email("john.smith@example.com") // Same as testTeacher
                .expertise("Chemistry")
                .build();

        when(teacherRepository.existsByEmail(newTeacher.getEmail())).thenReturn(true);

        // When & Then
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> teacherService.createTeacher(newTeacher)
        );
        assertEquals("teacher.email_exists", exception.getMessage());
        verify(teacherRepository).existsByEmail(newTeacher.getEmail());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should get all teachers successfully")
    void getAllTeachers_Success() {
        // Given
        when(teacherRepository.findAll()).thenReturn(testTeachers);

        // When
        List<Teacher> result = teacherService.getAllTeachers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTeachers.get(0).getId(), result.get(0).getId());
        assertEquals(testTeachers.get(1).getId(), result.get(1).getId());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no teachers exist")
    void getAllTeachers_EmptyList() {
        // Given
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Teacher> result = teacherService.getAllTeachers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should get teacher by ID successfully")
    void getTeacherById_Success() {
        // Given
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(testTeacher));

        // When
        Teacher result = teacherService.getTeacherById(teacherId);

        // Then
        assertNotNull(result);
        assertEquals(testTeacher.getId(), result.getId());
        assertEquals(testTeacher.getFirstName(), result.getFirstName());
        assertEquals(testTeacher.getLastName(), result.getLastName());
        assertEquals(testTeacher.getEmail(), result.getEmail());
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when teacher not found by ID")
    void getTeacherById_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        when(teacherRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> teacherService.getTeacherById(nonExistentId)
        );
        assertEquals("teacher.not_found", exception.getMessage());
        verify(teacherRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should handle repository exception during teacher creation")
    void createTeacher_RepositoryException_ThrowsException() {
        // Given
        Teacher newTeacher = Teacher.builder()
                .firstName("Error")
                .lastName("Teacher")
                .email("error@example.com")
                .expertise("Error expertise")
                .build();

        when(teacherRepository.existsByEmail(newTeacher.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> teacherService.createTeacher(newTeacher)
        );
        assertEquals("Database error", exception.getMessage());
        verify(teacherRepository).existsByEmail(newTeacher.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should handle repository exception during get all teachers")
    void getAllTeachers_RepositoryException_ThrowsException() {
        // Given
        when(teacherRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> teacherService.getAllTeachers()
        );
        assertEquals("Database error", exception.getMessage());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should handle repository exception during get teacher by ID")
    void getTeacherById_RepositoryException_ThrowsException() {
        // Given
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> teacherService.getTeacherById(teacherId)
        );
        assertEquals("Database error", exception.getMessage());
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    @DisplayName("Should create teacher with null optional fields")
    void createTeacher_WithNullOptionalFields_Success() {
        // Given
        Teacher teacherWithNulls = Teacher.builder()
                .firstName("Test")
                .lastName("Teacher")
                .email("test@example.com")
                .expertise(null)
                .build();

        Teacher savedTeacher = Teacher.builder()
                .firstName("Test")
                .lastName("Teacher")
                .email("test@example.com")
                .expertise(null)
                .build();

        when(teacherRepository.existsByEmail(teacherWithNulls.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // When
        Teacher result = teacherService.createTeacher(teacherWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(savedTeacher.getId(), result.getId());
        assertEquals(savedTeacher.getFirstName(), result.getFirstName());
        assertEquals(savedTeacher.getLastName(), result.getLastName());
        assertEquals(savedTeacher.getEmail(), result.getEmail());
        assertNull(result.getExpertise());
        verify(teacherRepository).existsByEmail(teacherWithNulls.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should create teacher with empty expertise")
    void createTeacher_WithEmptyexpertise_Success() {
        // Given
        Teacher teacherWithEmptyexpertise = Teacher.builder()
                .firstName("Empty")
                .lastName("expertise")
                .email("empty@example.com")
                .expertise("")
                .build();

        Teacher savedTeacher = Teacher.builder()
                .firstName("Empty")
                .lastName("expertise")
                .email("empty@example.com")
                .expertise("")
                .build();

        when(teacherRepository.existsByEmail(teacherWithEmptyexpertise.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // When
        Teacher result = teacherService.createTeacher(teacherWithEmptyexpertise);

        // Then
        assertNotNull(result);
        assertEquals("", result.getExpertise());
        verify(teacherRepository).existsByEmail(teacherWithEmptyexpertise.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should handle existsByEmail exception during teacher creation")
    void createTeacher_ExistsByEmailException_ThrowsException() {
        // Given
        Teacher newTeacher = Teacher.builder()
                .firstName("Error")
                .lastName("Exists")
                .email("error@example.com")
                .expertise("Error expertise")
                .build();

        when(teacherRepository.existsByEmail(newTeacher.getEmail())).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> teacherService.createTeacher(newTeacher)
        );
        assertEquals("Database error", exception.getMessage());
        verify(teacherRepository).existsByEmail(newTeacher.getEmail());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should get teacher with maximum valid ID")
    void getTeacherById_MaxId_Success() {
        // Given
        Long maxId = Long.MAX_VALUE;
        Teacher maxIdTeacher = Teacher.builder()
                .firstName("Max")
                .lastName("ID")
                .email("max@example.com")
                .expertise("Max expertise")
                .build();

        when(teacherRepository.findById(maxId)).thenReturn(Optional.of(maxIdTeacher));

        // When
        Teacher result = teacherService.getTeacherById(maxId);

        // Then
        assertNotNull(result);
        verify(teacherRepository).findById(maxId);
    }

    @Test
    @DisplayName("Should get teacher with minimum valid ID")
    void getTeacherById_MinId_Success() {
        // Given
        Long minId = 1L;
        Teacher minIdTeacher = Teacher.builder()
                .firstName("Min")
                .lastName("ID")
                .email("min@example.com")
                .expertise("Min expertise")
                .build();

        when(teacherRepository.findById(minId)).thenReturn(Optional.of(minIdTeacher));

        // When
        Teacher result = teacherService.getTeacherById(minId);

        // Then
        assertNotNull(result);
        verify(teacherRepository).findById(minId);
    }

    @Test
    @DisplayName("Should handle large number of teachers in getAllTeachers")
    void getAllTeachers_LargeNumberOfTeachers_Success() {
        // Given
        List<Teacher> largeTeacherList = Arrays.asList(
                Teacher.builder().firstName("Teacher 1").email("teacher1@example.com").build(),
                Teacher.builder().firstName("Teacher 2").email("teacher2@example.com").build(),
                Teacher.builder().firstName("Teacher 3").email("teacher3@example.com").build(),
                Teacher.builder().firstName("Teacher 4").email("teacher4@example.com").build(),
                Teacher.builder().firstName("Teacher 5").email("teacher5@example.com").build()
        );
        when(teacherRepository.findAll()).thenReturn(largeTeacherList);

        // When
        List<Teacher> result = teacherService.getAllTeachers();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should create teacher with very long name")
    void createTeacher_WithVeryLongName_Success() {
        // Given
        String longName = "A".repeat(1000); // Very long name
        Teacher teacherWithLongName = Teacher.builder()
                .firstName(longName)
                .lastName("Long")
                .email("longname@example.com")
                .expertise("Long Name expertise")
                .build();

        Teacher savedTeacher = Teacher.builder()
                .firstName(longName)
                .lastName("Long")
                .email("longname@example.com")
                .expertise("Long Name expertise")
                .build();

        when(teacherRepository.existsByEmail(teacherWithLongName.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // When
        Teacher result = teacherService.createTeacher(teacherWithLongName);

        // Then
        assertNotNull(result);
        assertEquals(longName, result.getFirstName());
        verify(teacherRepository).existsByEmail(teacherWithLongName.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should create teacher with special characters in email")
    void createTeacher_WithSpecialCharactersInEmail_Success() {
        // Given
        Teacher teacherWithSpecialEmail = Teacher.builder()
                .firstName("Special")
                .lastName("Email")
                .email("test.email+special@example-domain.com")
                .expertise("Special Email expertise")
                .build();

        Teacher savedTeacher = Teacher.builder()
                .firstName("Special")
                .lastName("Email")
                .email("test.email+special@example-domain.com")
                .expertise("Special Email expertise")
                .build();

        when(teacherRepository.existsByEmail(teacherWithSpecialEmail.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // When
        Teacher result = teacherService.createTeacher(teacherWithSpecialEmail);

        // Then
        assertNotNull(result);
        assertEquals("test.email+special@example-domain.com", result.getEmail());
        verify(teacherRepository).existsByEmail(teacherWithSpecialEmail.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should handle case-sensitive email check")
    void createTeacher_CaseSensitiveEmailCheck_Success() {
        // Given
        Teacher teacherWithDifferentCase = Teacher.builder()
                .firstName("Case")
                .lastName("Sensitive")
                .email("John.SMITH@example.com") // Different case from testTeacher
                .expertise("Case expertise")
                .build();

        Teacher savedTeacher = Teacher.builder()
                .firstName("Case")
                .lastName("Sensitive")
                .email("John.SMITH@example.com")
                .expertise("Case expertise")
                .build();

        when(teacherRepository.existsByEmail(teacherWithDifferentCase.getEmail())).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // When
        Teacher result = teacherService.createTeacher(teacherWithDifferentCase);

        // Then
        assertNotNull(result);
        assertEquals("John.SMITH@example.com", result.getEmail());
        verify(teacherRepository).existsByEmail(teacherWithDifferentCase.getEmail());
        verify(teacherRepository).save(any(Teacher.class));
    }
}
