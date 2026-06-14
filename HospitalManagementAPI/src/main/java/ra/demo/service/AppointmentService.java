package ra.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.demo.model.dto.request.AppointmentRequest;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.dto.response.AppointmentResponse;

public interface AppointmentService {

    AppointmentResponse create(
            AppointmentRequest request,
            String username
    );

    Page<AppointmentResponse> patientHistory(
            String username,
            Pageable pageable
    );

    Page<AppointmentResponse> doctorAppointments(
            String username,
            Pageable pageable
    );

    AppointmentResponse updateStatus(
            Long id,
            AppointmentStatusRequest request
    );

    AppointmentResponse updateStatusByDoctor(
            Long id,
            AppointmentStatusRequest request,
            String doctorUsername
    );

    Page<AppointmentResponse> findAll(
            Pageable pageable
    );
}