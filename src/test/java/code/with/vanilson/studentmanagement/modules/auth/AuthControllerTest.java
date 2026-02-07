package code.with.vanilson.studentmanagement.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import code.with.vanilson.studentmanagement.config.JwtUtils;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @MockBean
        private RefreshTokenService refreshTokenService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsService userDetailsService;

        @MockBean
        private AuthenticationProvider authenticationProvider;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /api/v1/auth/register - Success")
        @WithMockUser
        void register_Success() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .firstname("John")
                                .lastname("Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(Role.USER)
                                .build();

                AuthenticationResponse response = AuthenticationResponse.builder()
                                .accessToken("access-token")
                                .refreshToken("refresh-token")
                                .build();

                when(authService.register(any(RegisterRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Bad Request (Validation)")
        @WithMockUser
        void register_BadRequest() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .email("invalid-email")
                                .build();

                mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/authenticate - Success")
        @WithMockUser
        void authenticate_Success() throws Exception {
                AuthenticationRequest request = AuthenticationRequest.builder()
                                .email("john.doe@example.com")
                                .password("password123")
                                .build();

                AuthenticationResponse response = AuthenticationResponse.builder()
                                .accessToken("access-token")
                                .refreshToken("refresh-token")
                                .build();

                when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/auth/authenticate")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
        }

        @Test
        @DisplayName("POST /api/v1/auth/authenticate - Bad Request (Validation)")
        @WithMockUser
        void authenticate_BadRequest() throws Exception {
                AuthenticationRequest request = AuthenticationRequest.builder()
                                .email("invalid-email")
                                .build();

                mockMvc.perform(post("/api/v1/auth/authenticate")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/auth/refreshtoken - Success")
        @WithMockUser
        void refreshToken_Success() throws Exception {
                TokenRefreshRequest request = new TokenRefreshRequest();
                request.setRefreshToken("valid-refresh-token");

                RefreshToken refreshToken = new RefreshToken();
                User user = new User();
                user.setEmail("john.doe@example.com");
                refreshToken.setUser(user);

                when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
                when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
                when(jwtUtils.generateToken(user)).thenReturn("new-access-token");

                mockMvc.perform(post("/api/v1/auth/refreshtoken")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
        }
}
