package code.with.vanilson.studentmanagement.modules.teacher;

import code.with.vanilson.studentmanagement.common.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository repository;

    @InjectMocks
    private TeacherService service;

    @Test
    @DisplayName("Create Teacher - Should return Teacher when data is valid")
    void createTeacher_ShouldReturnTeacher_WhenDataIsValid() {
        // Arrange
        Teacher teacher = Teacher.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .expertise("Mathematics")
                .build();
        teacher.setId(1L);

        when(repository.existsByEmail(teacher.getEmail())).thenReturn(false);
        when(repository.save(any(Teacher.class))).thenReturn(teacher);

        // Act
        Teacher result = service.createTeacher(teacher);

        // Assert
        assertNotNull(result);
        assertEquals(teacher.getId(), result.getId());
        assertEquals(teacher.getEmail(), result.getEmail());
        verify(repository, times(1)).existsByEmail(teacher.getEmail());
        verify(repository, times(1)).save(teacher);
    }

    @Test
    @DisplayName("Create Teacher - Should throw ResourceAlreadyExistsException when email exists")
    void createTeacher_ShouldThrowResourceAlreadyExistsException_WhenEmailExists() {
        // Arrange
        Teacher teacher = Teacher.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .build();

        when(repository.existsByEmail(teacher.getEmail())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            service.createTeacher(teacher);
        });

        assertEquals("teacher.email_exists", exception.getMessage());
        assertArrayEquals(new Object[] { teacher.getEmail() }, exception.getArgs());
        verify(repository, times(1)).existsByEmail(teacher.getEmail());
        verify(repository, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Get All Teachers - Should return list of Teachers")
    void getAllTeachers_ShouldReturnListOfTeachers() {
        // Arrange
        Teacher teacher1 = Teacher.builder().firstName("Alice").build();
        teacher1.setId(1L);
        Teacher teacher2 = Teacher.builder().firstName("Bob").build();
        teacher2.setId(2L);
        when(repository.findAll()).thenReturn(List.of(teacher1, teacher2));

        // Act
        List<Teacher> result = service.getAllTeachers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }
}
