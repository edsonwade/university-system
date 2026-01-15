package code.with.vanilson.studentmanagement.modules.auth;

import code.with.vanilson.studentmanagement.common.dto.ApiResponse;
import code.with.vanilson.studentmanagement.common.exception.ResourceBadRequestException;
import code.with.vanilson.studentmanagement.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and token refresh")
public class AuthController {

    private final AuthService service;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    @io.swagger.v3.oas.annotations.Operation(summary = "Register a new user", description = "Creates a new user account and returns an access token and refresh token.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.register(request), "User registered successfully"));
    }

    @PostMapping("/authenticate")
    @io.swagger.v3.oas.annotations.Operation(summary = "Authenticate user", description = "Authenticates a user and returns an access token and refresh token.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User authenticated successfully")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.authenticate(request), "User authenticated successfully"));
    }

    @PostMapping("/refreshtoken")
    @io.swagger.v3.oas.annotations.Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateToken(user);
                    return ResponseEntity.ok(ApiResponse.success(
                            TokenRefreshResponse.builder()
                                    .accessToken(token)
                                    .refreshToken(requestRefreshToken)
                                    .build(),
                            "Token refreshed successfully"));
                })
                .orElseThrow(() -> new ResourceBadRequestException(
                        "auth.token_not_found"));
    }
}
