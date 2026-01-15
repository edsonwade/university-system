package code.with.vanilson.studentmanagement.modules.auth;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
