package ra.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.enums.RoleName;
import ra.demo.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminUserControllerTest {
    private final UserService service = mock(UserService.class);
    private final AdminUserController controller = new AdminUserController(service);

    @Test
    void create_shouldReturn201() {
        UserRequest r = new UserRequest();
        r.setUsername("doctor"); r.setEmail("d@h.com"); r.setFullName("Doctor"); r.setPassword("123456"); r.setRole(RoleName.DOCTOR);
        when(service.create(r)).thenReturn(UserResponse.builder().id(1L).username("doctor").role(RoleName.DOCTOR).active(true).build());

        var response = controller.create(r);

        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
        assertEquals("doctor", response.getBody().getData().getUsername());
    }

    @Test
    void list_shouldReturn200WithPage() {
        when(service.findAll(eq("doc"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(
                UserResponse.builder().id(1L).username("doctor").role(RoleName.DOCTOR).active(true).build()
        )));

        var response = controller.list("doc", 0, 5, "id");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().getContent().size());
    }

    @Test
    void delete_shouldReturn204() {
        var response = controller.delete(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).deactivate(1L);
    }
}
