package ra.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.demo.exception.ConflictException;
import ra.demo.exception.ForbiddenException;
import ra.demo.model.dto.request.AppointmentRequest;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.User;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.AppointmentRepository;
import ra.demo.repository.UserRepository;
import ra.demo.service.impl.AppointmentServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
    @Mock AppointmentRepository repo;
    @Mock UserRepository userRepo;
    @InjectMocks AppointmentServiceImpl service;

    @Test
    void create_shouldCreatePendingAppointment() {
        User patient = user(1L, "patient", RoleName.PATIENT);
        User doctor = user(2L, "doctor", RoleName.DOCTOR);
        AppointmentRequest r = new AppointmentRequest();
        r.setDoctorId(2L); r.setAppointmentTime(LocalDateTime.now().plusDays(2)); r.setSymptomDescription("Headache");
        when(userRepo.findByUsername("patient")).thenReturn(Optional.of(patient));
        when(userRepo.findById(2L)).thenReturn(Optional.of(doctor));
        when(repo.existsByDoctorAndAppointmentTimeAndStatusIn(eq(doctor), eq(r.getAppointmentTime()), anyList()))
                .thenReturn(false);
        when(repo.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0); a.setId(10L); return a;
        });

        var res = service.create(r, "patient");

        assertEquals(AppointmentStatus.PENDING, res.getStatus());
        assertEquals(2L, res.getDoctorId());
    }

    @Test
    void create_shouldThrowConflict_whenDoctorBusy() {
        User patient = user(1L, "patient", RoleName.PATIENT);
        User doctor = user(2L, "doctor", RoleName.DOCTOR);
        AppointmentRequest r = new AppointmentRequest();
        r.setDoctorId(2L); r.setAppointmentTime(LocalDateTime.now().plusDays(2)); r.setSymptomDescription("Headache");
        when(userRepo.findByUsername("patient")).thenReturn(Optional.of(patient));
        when(userRepo.findById(2L)).thenReturn(Optional.of(doctor));
        when(repo.existsByDoctorAndAppointmentTimeAndStatusIn(eq(doctor), eq(r.getAppointmentTime()), anyList()))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(r, "patient"));
    }

    @Test
    void updateStatusByDoctor_shouldForbidOtherDoctorAppointment() {
        User doctor = user(2L, "doctor", RoleName.DOCTOR);
        User otherDoctor = user(3L, "other", RoleName.DOCTOR);
        Appointment a = Appointment.builder().id(1L).doctor(otherDoctor).patient(user(1L,"p",RoleName.PATIENT))
                .status(AppointmentStatus.PENDING).appointmentTime(LocalDateTime.now().plusDays(1)).build();
        AppointmentStatusRequest r = new AppointmentStatusRequest(); r.setStatus(AppointmentStatus.APPROVED);
        when(userRepo.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(repo.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(ForbiddenException.class, () -> service.updateStatusByDoctor(1L, r, "doctor"));
    }

    @Test
    void updateStatus_shouldAllowPendingToApproved() {
        Appointment a = Appointment.builder().id(1L).doctor(user(2L,"d",RoleName.DOCTOR))
                .patient(user(1L,"p",RoleName.PATIENT)).status(AppointmentStatus.PENDING)
                .appointmentTime(LocalDateTime.now().plusDays(1)).build();
        AppointmentStatusRequest r = new AppointmentStatusRequest(); r.setStatus(AppointmentStatus.APPROVED);
        when(repo.findById(1L)).thenReturn(Optional.of(a));
        when(repo.save(a)).thenReturn(a);

        var res = service.updateStatus(1L, r);

        assertEquals(AppointmentStatus.APPROVED, res.getStatus());
    }

    private User user(Long id, String username, RoleName role) {
        return User.builder().id(id).username(username).email(username + "@h.com").fullName(username)
                .password("x").role(role).active(true).build();
    }
}
