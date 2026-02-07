package code.with.vanilson.studentmanagement.modules.auth;

import code.with.vanilson.studentmanagement.common.exception.ResourceAlreadyExistsException;
import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import code.with.vanilson.studentmanagement.config.JwtUtils;
import code.with.vanilson.studentmanagement.modules.notification.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

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
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;
    private User testUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        authRequest = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-123")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void register_Success() {
        // Given
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(testUser.getId())).thenReturn(refreshToken);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        AuthenticationResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("refresh-token-123", response.getRefreshToken());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendSimpleMessage(eq("john.doe@example.com"), eq("Welcome to University System"), anyString());
        verify(jwtUtils).generateToken(testUser);
        verify(refreshTokenService).createRefreshToken(testUser.getId());
    }

    @Test
    @DisplayName("Should register user with default USER role when role is null")
    void register_WithNullRole_Success() {
        // Given
        registerRequest.setRole(null);
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(testUser.getId())).thenReturn(refreshToken);
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        AuthenticationResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepository).save(argThat(user -> user.getRole() == Role.USER));
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExistsException when email already exists during registration")
    void register_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> authService.register(registerRequest)
        );
        assertEquals("auth.user_already_exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should successfully authenticate valid credentials")
    void authenticate_Success() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateToken(testUser)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(testUser.getId())).thenReturn(refreshToken);

        // When
        AuthenticationResponse response = authService.authenticate(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("refresh-token-123", response.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(authRequest.getEmail());
        verify(jwtUtils).generateToken(testUser);
        verify(refreshTokenService).createRefreshToken(testUser.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found during authentication")
    void authenticate_UserNotFound_ThrowsException() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.authenticate(authRequest)
        );
        assertEquals("auth.user_not_found", exception.getMessage());
        verify(jwtUtils, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).createRefreshToken(anyLong());
    }

    @Test
    @DisplayName("Should propagate authentication exception when credentials are invalid")
    void authenticate_InvalidCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(authRequest)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtUtils, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).createRefreshToken(anyLong());
    }

    @Disabled
    @Test
    @DisplayName("Should handle email service exception gracefully during registration")
    void register_EmailServiceException_StillRegistersUser() {
        // Given
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(testUser.getId())).thenReturn(refreshToken);
        doThrow(new RuntimeException("Email service failed"))
                .when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // When
        AuthenticationResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendSimpleMessage(anyString(), anyString(), anyString());
    }
}
