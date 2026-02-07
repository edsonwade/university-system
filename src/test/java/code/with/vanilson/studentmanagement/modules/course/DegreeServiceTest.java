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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DegreeService Unit Tests")
class DegreeServiceTest {

    @Mock
    private DegreeRepository degreeRepository;

    @InjectMocks
    private DegreeService degreeService;

    private Degree testDegree;
    private List<Degree> testDegrees;

    @BeforeEach
    void setUp() {
        testDegree = Degree.builder()
                .name("Bachelor of Computer Science")
                .department("Computer Science")
                .durationYears(4)
                .build();

        testDegrees = Arrays.asList(
                testDegree,
                Degree.builder()
                        .name("Bachelor of Mathematics")
                        .department("Mathematics")
                        .durationYears(3)
                        .build()
        );
    }

    @Test
    @DisplayName("Should create a new degree successfully")
    void createDegree_Success() {
        // Given
        Degree newDegree = Degree.builder()
                .name("Master of Science")
                .department("MSC")
                .durationYears(2)
                .build();

        Degree savedDegree = Degree.builder()
                .name("Master of Science")
                .department("MSC")
                .durationYears(2)
                .build();

        when(degreeRepository.save(any(Degree.class))).thenReturn(savedDegree);

        // When
        Degree result = degreeService.createDegree(newDegree);

        // Then
        assertNotNull(result);
        assertEquals(savedDegree.getId(), result.getId());
        assertEquals(savedDegree.getName(), result.getName());
        assertEquals(savedDegree.getDepartment(), result.getDepartment());
        assertEquals(savedDegree.getDurationYears(), result.getDurationYears());

        verify(degreeRepository).save(any(Degree.class));
    }

    @Test
    @DisplayName("Should get all degrees successfully")
    void getAllDegrees_Success() {
        // Given
        when(degreeRepository.findAll()).thenReturn(testDegrees);

        // When
        List<Degree> result = degreeService.getAllDegrees();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testDegrees.get(0).getId(), result.get(0).getId());
        assertEquals(testDegrees.get(1).getId(), result.get(1).getId());
        verify(degreeRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no degrees exist")
    void getAllDegrees_EmptyList() {
        // Given
        when(degreeRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Degree> result = degreeService.getAllDegrees();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(degreeRepository).findAll();
    }

    @Test
    @DisplayName("Should get degree by ID successfully")
    void getDegreeById_Success() {
        // Given
        Long degreeId = 1L;
        when(degreeRepository.findById(degreeId)).thenReturn(Optional.of(testDegree));

        // When
        Degree result = degreeService.getDegreeById(degreeId);

        // Then
        assertNotNull(result);
        assertEquals(testDegree.getId(), result.getId());
        assertEquals(testDegree.getName(), result.getName());
        assertEquals(testDegree.getDepartment(), result.getDepartment());
        assertEquals(testDegree.getDurationYears(), result.getDurationYears());

        verify(degreeRepository).findById(degreeId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when degree not found by ID")
    void getDegreeById_NotFound_ThrowsException() {
        // Given
        Long nonExistentId = 999L;
        when(degreeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> degreeService.getDegreeById(nonExistentId)
        );
        assertEquals("degree.not_found", exception.getMessage());
        verify(degreeRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should handle repository exception during degree creation")
    void createDegree_RepositoryException_ThrowsException() {
        // Given
        Degree newDegree = Degree.builder()
                .name("Error Degree")
                .department("No department")
                .build();

        when(degreeRepository.save(any(Degree.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> degreeService.createDegree(newDegree)
        );
        assertEquals("Database error", exception.getMessage());
        verify(degreeRepository).save(any(Degree.class));
    }

    @Test
    @DisplayName("Should handle repository exception during get all degrees")
    void getAllDegrees_RepositoryException_ThrowsException() {
        // Given
        when(degreeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> degreeService.getAllDegrees()
        );
        assertEquals("Database error", exception.getMessage());
        verify(degreeRepository).findAll();
    }

    @Test
    @DisplayName("Should handle repository exception during get degree by ID")
    void getDegreeById_RepositoryException_ThrowsException() {
        // Given
        Long degreeId = 1L;
        when(degreeRepository.findById(degreeId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> degreeService.getDegreeById(degreeId)
        );
        assertEquals("Database error", exception.getMessage());
        verify(degreeRepository).findById(degreeId);
    }

    @Test
    @DisplayName("Should create degree with null optional fields")
    void createDegree_WithNullOptionalFields_Success() {
        // Given
        Degree degreeWithNulls = Degree.builder()
                .name("Test Degree")
                .department(null)
                .durationYears(4)
                .build();

        Degree savedDegree = Degree.builder()
                .name("Test Degree")
                .department(null)
                .durationYears(4)
                .build();

        when(degreeRepository.save(any(Degree.class))).thenReturn(savedDegree);

        // When
        Degree result = degreeService.createDegree(degreeWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(savedDegree.getId(), result.getId());
        assertEquals(savedDegree.getName(), result.getName());
        assertEquals(savedDegree.getDepartment(), result.getDepartment());
        assertEquals(savedDegree.getDurationYears(), result.getDurationYears());

        verify(degreeRepository).save(any(Degree.class));
    }


}
