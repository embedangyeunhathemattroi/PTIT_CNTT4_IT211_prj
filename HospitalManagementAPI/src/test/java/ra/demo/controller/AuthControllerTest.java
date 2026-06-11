package ra.demo.controller;

import org.junit.jupiter.api.Test;
import ra.demo.model.dto.request.LoginRequest;
import ra.demo.model.dto.request.RefreshTokenRequest;
import ra.demo.model.dto.response.JwtResponse;
import ra.demo.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    private final AuthService service = mock(AuthService.class);
    private final AuthController controller = new AuthController(service);

    @Test
    void login_shouldReturn200AndToken() {
        LoginRequest r = new LoginRequest(); r.setUsername("admin"); r.setPassword("123456");
        when(service.login(r)).thenReturn(JwtResponse.builder().accessToken("access").refreshToken("refresh").tokenType("Bearer").build());

        var response = controller.login(r);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("access", response.getBody().getData().getAccessToken());
    }

    @Test
    void refresh_shouldReturnNewAccessToken() {
        RefreshTokenRequest r = new RefreshTokenRequest(); r.setRefreshToken("refresh");
        when(service.refresh(r)).thenReturn(JwtResponse.builder().accessToken("new-access").refreshToken("refresh").tokenType("Bearer").build());

        var response = controller.refresh(r);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("new-access", response.getBody().getData().getAccessToken());
    }
}
