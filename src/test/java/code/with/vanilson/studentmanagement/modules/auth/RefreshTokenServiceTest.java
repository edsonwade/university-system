package code.with.vanilson.studentmanagement.modules.auth;

import code.with.vanilson.studentmanagement.common.exception.ResourceBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "refreshTokenDurationMs", 604800000L);
    }

    @Test
    @DisplayName("Find By Token - Should return Optional of RefreshToken")
    void findByToken_ShouldReturnOptionalOfRefreshToken() {
        // Arrange
        String token = "test-token";
        RefreshToken refreshToken = new RefreshToken();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        // Act
        Optional<RefreshToken> result = service.findByToken(token);

        // Assert
        assertTrue(result.isPresent());
        verify(refreshTokenRepository, times(1)).findByToken(token);
    }

    @Test
    @DisplayName("Create Refresh Token - Should return new RefreshToken")
    void createRefreshToken_ShouldReturnNewRefreshToken() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefreshToken result = service.createRefreshToken(userId);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Verify Expiration - Should return token when not expired")
    void verifyExpiration_ShouldReturnToken_WhenNotExpired() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        // Act
        RefreshToken result = service.verifyExpiration(token);

        // Assert
        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Verify Expiration - Should throw ResourceBadRequestException and delete token when expired")
    void verifyExpiration_ShouldThrowException_WhenExpired() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(3600));

        // Act & Assert
        ResourceBadRequestException exception = assertThrows(ResourceBadRequestException.class, () -> {
            service.verifyExpiration(token);
        });

        assertEquals("auth.token_expired", exception.getMessage());
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    @DisplayName("Delete By User ID - Should call repository deleteByUser")
    void deleteByUserId_ShouldCallRepositoryDeleteByUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);

        // Act
        int result = service.deleteByUserId(userId);

        // Assert
        assertEquals(1, result);
        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}
