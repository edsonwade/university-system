package code.with.vanilson.studentmanagement.modules.auth;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import code.with.vanilson.studentmanagement.config.JwtUtils;
import code.with.vanilson.studentmanagement.modules.notification.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService service;

    @Test
    @DisplayName("Register - Should return AuthenticationResponse when data is valid")
    void register_ShouldReturnAuthenticationResponse_WhenDataIsValid() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        user.setId(1L);

        RefreshToken refreshToken = RefreshToken.builder().token("refreshToken").build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user); // Actually save returns void or User, but mock handles
                                                                 // it
        when(jwtUtils.generateToken(any(User.class))).thenReturn("jwtToken");
        when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(refreshToken);

        // Act
        AuthenticationResponse result = service.register(request);

        // Assert
        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(repository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendSimpleMessage(eq(user.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("Authenticate - Should return AuthenticationResponse when credentials are valid")
    void authenticate_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("password")
                .build();

        User user = User.builder()
                .email("john.doe@example.com")
                .build();
        user.setId(1L);

        RefreshToken refreshToken = RefreshToken.builder().token("refreshToken").build();

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("jwtToken");
        when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(refreshToken);

        // Act
        AuthenticationResponse result = service.authenticate(request);

        // Assert
        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("Authenticate - Should throw ResourceNotFoundException when user not found")
    void authenticate_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("password")
                .build();

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.authenticate(request);
        });

        assertEquals("auth.user_not_found", exception.getMessage());
        assertArrayEquals(new Object[] { request.getEmail() }, exception.getArgs());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository, times(1)).findByEmail(request.getEmail());
    }
}
