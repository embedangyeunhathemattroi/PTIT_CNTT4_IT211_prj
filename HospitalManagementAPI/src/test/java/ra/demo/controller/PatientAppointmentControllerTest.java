package ra.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ra.demo.model.dto.request.AppointmentRequest;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.service.AppointmentService;
import ra.demo.service.MedicalRecordService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientAppointmentControllerTest {
    private final AppointmentService appointmentService = mock(AppointmentService.class);
    private final MedicalRecordService medicalRecordService = mock(MedicalRecordService.class);
    private final PatientAppointmentController controller = new PatientAppointmentController(appointmentService, medicalRecordService);

    @Test
    void create_shouldReturn201() {
        AppointmentRequest r = new AppointmentRequest();
        r.setDoctorId(2L); r.setAppointmentTime(LocalDateTime.now().plusDays(1)); r.setSymptomDescription("Fever");
        var auth = new UsernamePasswordAuthenticationToken("patient", null);
        when(appointmentService.create(r, "patient")).thenReturn(AppointmentResponse.builder().id(1L).status(AppointmentStatus.PENDING).build());

        var response = controller.create(r, auth);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(AppointmentStatus.PENDING, response.getBody().getData().getStatus());
    }
}
