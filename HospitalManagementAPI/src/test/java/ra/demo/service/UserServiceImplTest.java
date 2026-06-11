package ra.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.demo.exception.ConflictException;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.entity.User;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.UserRepository;
import ra.demo.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserRepository repo;
    @Mock PasswordEncoder encoder;
    @InjectMocks UserServiceImpl service;

    @Test
    void create_shouldReturnUserResponse_whenValidRequest() {
        UserRequest r = new UserRequest();
        r.setUsername("doctor1"); r.setEmail("doctor1@hospital.com"); r.setFullName("Doctor One");
        r.setPassword("123456"); r.setRole(RoleName.DOCTOR); r.setActive(true);
        when(repo.existsByUsername("doctor1")).thenReturn(false);
        when(repo.existsByEmail("doctor1@hospital.com")).thenReturn(false);
        when(encoder.encode("123456")).thenReturn("encoded");
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0); u.setId(1L); return u;
        });

        var res = service.create(r);

        assertEquals("doctor1", res.getUsername());
        assertEquals(RoleName.DOCTOR, res.getRole());
        verify(repo).save(any(User.class));
    }

    @Test
    void create_shouldThrowConflict_whenUsernameExists() {
        UserRequest r = new UserRequest();
        r.setUsername("admin"); r.setEmail("admin@hospital.com"); r.setFullName("Admin");
        r.setPassword("123456"); r.setRole(RoleName.ADMIN);
        when(repo.existsByUsername("admin")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(r));
        verify(repo, never()).save(any());
    }

    @Test
    void findAll_shouldMapByStreamAndReturnPage() {
        User u = User.builder().id(1L).username("patient").email("p@hospital.com")
                .fullName("Patient").password("x").role(RoleName.PATIENT).active(true).build();
        when(repo.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(eq("pat"), eq("pat"), any()))
                .thenReturn(new PageImpl<>(List.of(u), PageRequest.of(0, 5), 1));

        var page = service.findAll("pat", PageRequest.of(0, 5));

        assertEquals(1, page.getTotalElements());
        assertEquals("patient", page.getContent().get(0).getUsername());
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        User u = User.builder().id(1L).username("u").email("u@h.com").fullName("U")
                .password("x").role(RoleName.PATIENT).active(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(u));

        service.deactivate(1L);

        assertFalse(u.isActive());
        verify(repo).save(u);
    }
}
