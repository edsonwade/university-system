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
class DegreeServiceTest {

    @Mock
    private DegreeRepository repository;

    @InjectMocks
    private DegreeService service;

    @Test
    @DisplayName("Create Degree - Should return Degree when data is valid")
    void createDegree_ShouldReturnDegree_WhenDataIsValid() {
        // Arrange
        Degree degree = Degree.builder()
                .name("Computer Science")
                .build();
        degree.setId(1L);

        when(repository.save(any(Degree.class))).thenReturn(degree);

        // Act
        Degree result = service.createDegree(degree);

        // Assert
        assertNotNull(result);
        assertEquals(degree.getId(), result.getId());
        assertEquals(degree.getName(), result.getName());
        verify(repository, times(1)).save(degree);
    }

    @Test
    @DisplayName("Get All Degrees - Should return list of Degrees")
    void getAllDegrees_ShouldReturnListOfDegrees() {
        // Arrange
        Degree degree1 = Degree.builder().name("CS").build();
        degree1.setId(1L);
        Degree degree2 = Degree.builder().name("SE").build();
        degree2.setId(2L);
        when(repository.findAll()).thenReturn(List.of(degree1, degree2));

        // Act
        List<Degree> result = service.getAllDegrees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get Degree By ID - Should return Degree when degree exists")
    void getDegreeById_ShouldReturnDegree_WhenDegreeExists() {
        // Arrange
        Long degreeId = 1L;
        Degree degree = Degree.builder()
                .name("CS")
                .build();
        degree.setId(degreeId);

        when(repository.findById(degreeId)).thenReturn(Optional.of(degree));

        // Act
        Degree result = service.getDegreeById(degreeId);

        // Assert
        assertNotNull(result);
        assertEquals(degreeId, result.getId());
        verify(repository, times(1)).findById(degreeId);
    }

    @Test
    @DisplayName("Get Degree By ID - Should throw ResourceNotFoundException when degree does not exist")
    void getDegreeById_ShouldThrowResourceNotFoundException_WhenDegreeDoesNotExist() {
        // Arrange
        Long degreeId = 1L;
        when(repository.findById(degreeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.getDegreeById(degreeId);
        });

        assertEquals("degree.not_found", exception.getMessage());
        assertArrayEquals(new Object[] { degreeId }, exception.getArgs());
        verify(repository, times(1)).findById(degreeId);
    }
}
