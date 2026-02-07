package code.with.vanilson.studentmanagement.modules.course;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private List<Course> testCourses;

    @BeforeEach
    void setUp() {
        testCourse = Course.builder()
                .title("Introduction to Computer Science")
                .credits(3)
                .description("Introduction to Computer Science")
                .build();

        testCourses = Arrays.asList(
                testCourse,
                Course.builder()
                        .title("Introduction to Mathematics")
                        .credits(4)
                        .description("Introduction to Mathematics")
                        .build()
        );
    }

    @Test
    @DisplayName("Should create a new course successfully")
    void createCourse_Success() {
        // Given
        Course newCourse = Course.builder()
                .title("Introduction to Physics")
                .credits(3)
                .description("Introduction to Physics")
                .build();

        Course savedCourse = Course.builder()
                .title("Introduction to Physics")
                .credits(3)
                .description("Introduction to Physics")
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        Course result = courseService.createCourse(newCourse);

        // Then
        assertNotNull(result);
        assertEquals(savedCourse.getId(), result.getId());
        assertEquals(savedCourse.getTitle(), result.getTitle());
        assertEquals(savedCourse.getCredits(), result.getCredits());
        assertEquals(savedCourse.getDescription(), result.getDescription());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should get all courses successfully")
    void getAllCourses_Success() {
        // Given
        when(courseRepository.findAll()).thenReturn(testCourses);

        // When
        List<Course> result = courseService.getAllCourses();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCourses.get(0).getId(), result.get(0).getId());
        assertEquals(testCourses.get(1).getId(), result.get(1).getId());
        verify(courseRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no courses exist")
    void getAllCourses_EmptyList() {
        // Given
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Course> result = courseService.getAllCourses();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository).findAll();
    }

    @Test
    @DisplayName("Should get course by ID successfully")
    void getCourseById_Success() {
        // Given
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));

        // When
        Course result = courseService.getCourseById(courseId);

        // Then
        assertNotNull(result);
        assertEquals(testCourse.getId(), result.getId());
        assertEquals(testCourse.getCredits(), result.getCredits());
        assertEquals(testCourse.getDescription(), result.getDescription());
        verify(courseRepository).findById(courseId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course not found by ID")
    void getCourseById_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        when(courseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.getCourseById(nonExistentId)
        );
        assertEquals("course.not_found", exception.getMessage());
        verify(courseRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should handle repository exception during course creation")
    void createCourse_RepositoryException_ThrowsException() {
        // Given
        Course newCourse = Course.builder()
                .title("Introduction to Computer Science")
                .credits(3)
                .description("Introduction to Computer Science")
                .build();

        when(courseRepository.save(any(Course.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.createCourse(newCourse)
        );
        assertEquals("Database error", exception.getMessage());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should handle repository exception during get all courses")
    void getAllCourses_RepositoryException_ThrowsException() {
        // Given
        when(courseRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.getAllCourses()
        );
        assertEquals("Database error", exception.getMessage());
        verify(courseRepository).findAll();
    }

    @Test
    @DisplayName("Should handle repository exception during get course by ID")
    void getCourseById_RepositoryException_ThrowsException() {
        // Given
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.getCourseById(courseId)
        );
        assertEquals("Database error", exception.getMessage());
        verify(courseRepository).findById(courseId);
    }

    @Test
    @DisplayName("Should create course with null optional fields")
    void createCourse_WithNullOptionalFields_Success() {
        // Given
        Course courseWithNulls = Course.builder()
                .title("no course")
                .credits(3)
                .description(null)
                .build();

        Course savedCourse = Course.builder()
                .title("no course")
                .credits(3)
                .description(null)
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        Course result = courseService.createCourse(courseWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(savedCourse.getId(), result.getId());
        assertEquals(savedCourse.getTitle(), result.getTitle());
        assertEquals(savedCourse.getCredits(), result.getCredits());
        assertNull(result.getDescription());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should create course with zero credits")
    void createCourse_WithZeroCredits_Success() {
        // Given
        Course courseWithZeroCredits = Course.builder()
                .title("Computer Science")
                .credits(0)
                .description("Course with no credits")
                .build();

        Course savedCourse = Course.builder()
                .title("Computer Science")
                .credits(0)
                .description("Course with no credits")
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        Course result = courseService.createCourse(courseWithZeroCredits);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getCredits());
        assertEquals("Computer Science", result.getTitle());
        assertEquals("Course with no credits", result.getDescription());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should create course with negative credits")
    void createCourse_WithNegativeCredits_Success() {
        // Given
        Course courseWithNegativeCredits = Course.builder()
                .title("Negative Credit Course")
                .credits(-1)
                .description("Course with negative credits")
                .build();

        Course savedCourse = Course.builder()
                .title("Negative Credit Course")
                .credits(-1)
                .description("Course with negative credits")
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        Course result = courseService.createCourse(courseWithNegativeCredits);

        // Then
        assertNotNull(result);
        assertEquals(-1, result.getCredits());
        assertEquals("Negative Credit Course", result.getTitle());
        assertEquals("Course with negative credits", result.getDescription());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should create course with empty description")
    void createCourse_WithEmptyDescription_Success() {
        // Given
        Course courseWithEmptyDescription = Course.builder()
                .title("Empty Description Course")
                .credits(2)
                .description("")
                .build();

        Course savedCourse = Course.builder()
                .title("Empty Description Course")
                .credits(2)
                .description("")
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        Course result = courseService.createCourse(courseWithEmptyDescription);

        // Then
        assertNotNull(result);
        assertEquals("Empty Description Course", result.getTitle());
        assertEquals(2, result.getCredits());
        assertEquals("", result.getDescription());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should get course with maximum valid ID")
    void getCourseById_MaxId_Success() {
        // Given
        Long maxId = Long.MAX_VALUE;
        Course maxIdCourse = Course.builder()
                .title("Max ID Course")
                .credits(1)
                .description("Course with maximum ID")
                .build();

        when(courseRepository.findById(maxId)).thenReturn(Optional.of(maxIdCourse));

        // When
        Course result = courseService.getCourseById(maxId);

        // Then
        assertNotNull(result);
        assertEquals("Max ID Course", result.getTitle());
        assertEquals(1, result.getCredits());
        assertEquals("Course with maximum ID", result.getDescription());
        verify(courseRepository).findById(maxId);
    }

    @Test
    @DisplayName("Should get course with minimum valid ID")
    void getCourseById_MinId_Success() {
        // Given
        Long minId = 1L;
        Course minIdCourse = Course.builder()
                .title("Min ID Course")
                .credits(1)
                .description("Course with minimum ID")
                .build();

        when(courseRepository.findById(minId)).thenReturn(Optional.of(minIdCourse));

        // When
        Course result = courseService.getCourseById(minId);

        // Then
        assertNotNull(result);
        assertEquals("Min ID Course", result.getTitle());
        assertEquals(1, result.getCredits());
        assertEquals("Course with minimum ID", result.getDescription());
        verify(courseRepository).findById(minId);
    }

    @Test
    @DisplayName("Should handle large number of courses in getAllCourses")
    void getAllCourses_LargeNumberOfCourses_Success() {
        // Given
        List<Course> largeCourseList = Arrays.asList(
                Course.builder().title("Course 1").credits(1).description("C1").build(),
                Course.builder().title("Course 2").credits(2).description("C2").build(),
                Course.builder().title("Course 3").credits(3).description("C3").build(),
                Course.builder().title("Course 4").credits(4).description("C4").build(),
                Course.builder().title("Course 5").credits(5).description("C5").build()
        );
        when(courseRepository.findAll()).thenReturn(largeCourseList);

        // When
        List<Course> result = courseService.getAllCourses();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(largeCourseList, result);
        verify(courseRepository).findAll();
    }
}
