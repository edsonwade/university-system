package code.with.vanilson.studentmanagement.modules.auth;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    @jakarta.validation.constraints.NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
