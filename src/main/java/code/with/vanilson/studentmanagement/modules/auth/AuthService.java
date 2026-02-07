package code.with.vanilson.studentmanagement.modules.auth;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import code.with.vanilson.studentmanagement.config.JwtUtils;
import code.with.vanilson.studentmanagement.modules.notification.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;
        private final RefreshTokenService refreshTokenService;
        private final EmailService emailService;

        public AuthenticationResponse register(RegisterRequest request) {
                if (repository.findByEmail(request.getEmail()).isPresent()) {
                    throw new code.with.vanilson.studentmanagement.common.exception.ResourceAlreadyExistsException("auth.user_already_exists", request.getEmail());
                }
                var user = User.builder()
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(request.getRole() != null ? request.getRole() : Role.USER)
                                .build();
                user = repository.save(user);

                // Send welcome email
                emailService.sendSimpleMessage(user.getEmail(), "Welcome to University System",
                                "Hello " + user.getFirstname() + ",\n\nYour account has been created successfully.");

                var jwtToken = jwtUtils.generateToken(user);
                var refreshToken = refreshTokenService.createRefreshToken(user.getId());
                return new AuthenticationResponse(jwtToken, refreshToken.getToken());
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("auth.user_not_found",
                                                request.getEmail()));
                var jwtToken = jwtUtils.generateToken(user);
                var refreshToken = refreshTokenService.createRefreshToken(user.getId());
                return new AuthenticationResponse(jwtToken, refreshToken.getToken());
        }
}
