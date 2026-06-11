package ra.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ra.demo.exception.ApiException;
import ra.demo.model.dto.request.MedicalRecordRequest;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.MedicalRecord;
import ra.demo.model.entity.User;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.AppointmentRepository;
import ra.demo.repository.MedicalRecordRepository;
import ra.demo.repository.UserRepository;
import ra.demo.service.impl.MedicalRecordServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {
    @Mock MedicalRecordRepository repo;
    @Mock AppointmentRepository appointmentRepo;
    @Mock UserRepository userRepo;
    @Mock CloudStorageService cloudStorageService;
    @InjectMocks MedicalRecordServiceImpl service;

    @Test
    void upload_shouldSaveCloudinaryUrl_whenValid() {
        User patient = user(1L, "patient", RoleName.PATIENT);
        User doctor = user(2L, "doctor", RoleName.DOCTOR);
        Appointment a = Appointment.builder().id(1L).patient(patient).doctor(doctor)
                .appointmentTime(LocalDateTime.now()).status(AppointmentStatus.APPROVED).build();
        MedicalRecordRequest r = new MedicalRecordRequest(); r.setAppointmentId(1L); r.setDiagnosis("Flu");
        MockMultipartFile file = new MockMultipartFile("file", "record.pdf", "application/pdf", "hello".getBytes());
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(a));
        when(userRepo.findByUsername("doctor")).thenReturn(Optional.of(doctor));
        when(repo.existsByAppointment(a)).thenReturn(false);
        when(cloudStorageService.uploadMedicalRecord(file)).thenReturn("https://cloudinary/record.pdf");
        when(repo.save(any(MedicalRecord.class))).thenAnswer(inv -> {
            MedicalRecord m = inv.getArgument(0); m.setId(1L); return m;
        });

        var res = service.upload(r, file, "doctor");

        assertEquals("https://cloudinary/record.pdf", res.getFileUrl());
        assertEquals("Flu", res.getDiagnosis());
    }

    @Test
    void upload_shouldRejectFileOver10Mb() {
        MedicalRecordRequest r = new MedicalRecordRequest(); r.setAppointmentId(1L); r.setDiagnosis("Flu");
        byte[] big = new byte[10 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile("file", "record.pdf", "application/pdf", big);

        assertThrows(ApiException.class, () -> service.upload(r, file, "doctor"));
        verifyNoInteractions(cloudStorageService);
    }

    private User user(Long id, String username, RoleName role) {
        return User.builder().id(id).username(username).email(username + "@h.com").fullName(username)
                .password("x").role(role).active(true).build();
    }
}
