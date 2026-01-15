package code.with.vanilson.studentmanagement.modules.course;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository repository;

    @InjectMocks
    private CourseService service;

    @Test
    @DisplayName("Create Course - Should return Course when data is valid")
    void createCourse_ShouldReturnCourse_WhenDataIsValid() {
        // Arrange
        Course course = Course.builder()
                .title("Introduction to Java")
                .description("Basic Java Programming")
                .credits(3)
                .build();
        course.setId(1L);

        when(repository.save(any(Course.class))).thenReturn(course);

        // Act
        Course result = service.createCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals(course.getId(), result.getId());
        assertEquals(course.getTitle(), result.getTitle());
        verify(repository, times(1)).save(course);
    }

    @Test
    @DisplayName("Get All Courses - Should return list of Courses")
    void getAllCourses_ShouldReturnListOfCourses() {
        // Arrange
        Course course1 = Course.builder().title("Java").build();
        course1.setId(1L);
        Course course2 = Course.builder().title("Python").build();
        course2.setId(2L);
        when(repository.findAll()).thenReturn(List.of(course1, course2));

        // Act
        List<Course> result = service.getAllCourses();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get Course By ID - Should return Course when course exists")
    void getCourseById_ShouldReturnCourse_WhenCourseExists() {
        // Arrange
        Long courseId = 1L;
        Course course = Course.builder()
                .title("Java")
                .build();
        course.setId(courseId);

        when(repository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        Course result = service.getCourseById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(repository, times(1)).findById(courseId);
    }

    @Test
    @DisplayName("Get Course By ID - Should throw ResourceNotFoundException when course does not exist")
    void getCourseById_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {
        // Arrange
        Long courseId = 1L;
        when(repository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.getCourseById(courseId);
        });

        assertEquals("course.not_found", exception.getMessage());
        assertArrayEquals(new Object[] { courseId }, exception.getArgs());
        verify(repository, times(1)).findById(courseId);
    }
}
