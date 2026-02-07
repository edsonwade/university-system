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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Unit Tests")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken testRefreshToken;
    private final Long TEST_REFRESH_TOKEN_DURATION = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .build();

        testRefreshToken = RefreshToken.builder()
                .id(1L)
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .expiryDate(Instant.now().plusMillis(TEST_REFRESH_TOKEN_DURATION))
                .build();

        // Set the private field using reflection
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", TEST_REFRESH_TOKEN_DURATION);
    }

    @Test
    @DisplayName("Should find refresh token by token")
    void findByToken_Success() {
        // Given
        String token = testRefreshToken.getToken();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(testRefreshToken));

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testRefreshToken, result.get());
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("Should return empty when token not found")
    void findByToken_NotFound() {
        // Given
        String nonExistentToken = "non-existent-token";
        when(refreshTokenRepository.findByToken(nonExistentToken)).thenReturn(Optional.empty());

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken(nonExistentToken);

        // Then
        assertFalse(result.isPresent());
        verify(refreshTokenRepository).findByToken(nonExistentToken);
    }

    @Test
    @DisplayName("Should create new refresh token for user")
    void createRefreshToken_Success() {
        // Given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // When
        RefreshToken result = refreshTokenService.createRefreshToken(userId);

        // Then
        assertNotNull(result);
        assertEquals(testRefreshToken, result);
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during refresh token creation")
    void createRefreshToken_UserNotFound_ThrowsException() {
        // Given
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> refreshTokenService.createRefreshToken(nonExistentUserId));
        verify(userRepository).findById(nonExistentUserId);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should verify expiration for valid token")
    void verifyExpiration_ValidToken() {
        // Given
        Instant futureExpiry = Instant.now().plusMillis(TEST_REFRESH_TOKEN_DURATION);
        testRefreshToken.setExpiryDate(futureExpiry);

        // When
        RefreshToken result = refreshTokenService.verifyExpiration(testRefreshToken);

        // Then
        assertEquals(testRefreshToken, result);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should throw exception and delete expired token")
    void verifyExpiration_ExpiredToken_ThrowsException() {
        // Given
        Instant pastExpiry = Instant.now().minusMillis(1000);
        testRefreshToken.setExpiryDate(pastExpiry);

        // When & Then
        ResourceBadRequestException exception = assertThrows(
                ResourceBadRequestException.class,
                () -> refreshTokenService.verifyExpiration(testRefreshToken)
        );
        assertEquals("auth.token_expired", exception.getMessage());
        verify(refreshTokenRepository).delete(testRefreshToken);
    }

    @Test
    @DisplayName("Should delete refresh tokens by user id")
    void deleteByUserId_Success() {
        // Given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.deleteByUser(testUser)).thenReturn(1);

        // When
        int result = refreshTokenService.deleteByUserId(userId);

        // Then
        assertEquals(1, result);
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).deleteByUser(testUser);
    }

    @Test
    @DisplayName("Should return zero when no tokens exist for user")
    void deleteByUserId_NoTokens() {
        // Given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.deleteByUser(testUser)).thenReturn(0);

        // When
        int result = refreshTokenService.deleteByUserId(userId);

        // Then
        assertEquals(0, result);
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).deleteByUser(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found during token deletion")
    void deleteByUserId_UserNotFound_ThrowsException() {
        // Given
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> refreshTokenService.deleteByUserId(nonExistentUserId));
        verify(userRepository).findById(nonExistentUserId);
        verify(refreshTokenRepository, never()).deleteByUser(any(User.class));
    }

    @Test
    @DisplayName("Should create refresh token with correct expiry date")
    void createRefreshToken_CorrectExpiryDate() {
        // Given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            return invocation.<RefreshToken>getArgument(0);
        });

        // When
        RefreshToken result = refreshTokenService.createRefreshToken(userId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getExpiryDate());
        
        // Verify the expiry date is approximately correct (within 1 second tolerance)
        Instant expectedExpiry = Instant.now().plusMillis(TEST_REFRESH_TOKEN_DURATION);
        long difference = Math.abs(result.getExpiryDate().toEpochMilli() - expectedExpiry.toEpochMilli());
        assertTrue(difference < 1000, "Expiry date should be within 1 second of expected time");
        
        verify(userRepository).findById(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should create refresh token with unique token string")
    void createRefreshToken_UniqueToken() {
        // Given
        Long userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            return invocation.<RefreshToken>getArgument(0);
        });

        // When
        RefreshToken result1 = refreshTokenService.createRefreshToken(userId);
        RefreshToken result2 = refreshTokenService.createRefreshToken(userId);

        // Then
        assertNotNull(result1.getToken());
        assertNotNull(result2.getToken());
        assertNotEquals(result1.getToken(), result2.getToken(), "Each refresh token should have a unique token string");
        
        verify(userRepository, times(2)).findById(userId);
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }
}
