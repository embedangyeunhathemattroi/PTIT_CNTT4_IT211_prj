package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.demo.exception.ApiException;
import ra.demo.exception.ConflictException;
import ra.demo.exception.ForbiddenException;
import ra.demo.mapper.ResponseMapper;
import ra.demo.model.dto.request.AppointmentRequest;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.User;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.model.enums.RoleName;
import ra.demo.repository.AppointmentRepository;
import ra.demo.repository.UserRepository;
import ra.demo.service.AppointmentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repo;
    private final UserRepository userRepo;

    @Override
    public AppointmentResponse create(AppointmentRequest r, String username) {

        User patient = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Patient not found"));

        User doctor = userRepo.findById(r.getDoctorId())
                .orElseThrow(() -> new ApiException("Doctor not found"));

        if (doctor.getRole() != RoleName.DOCTOR) {
            throw new ApiException("Selected user is not doctor");
        }

        if (repo.existsByDoctorAndAppointmentTimeAndStatusIn(
                doctor,
                r.getAppointmentTime(),
                List.of(
                        AppointmentStatus.PENDING,
                        AppointmentStatus.APPROVED
                )
        )) {
            throw new ConflictException(
                    "Doctor already has appointment at this time"
            );
        }

        Appointment appointment = repo.save(
                Appointment.builder()
                        .patient(patient)
                        .doctor(doctor)
                        .appointmentTime(r.getAppointmentTime())
                        .symptomDescription(r.getSymptomDescription())
                        .status(AppointmentStatus.PENDING)
                        .build()
        );

        return ResponseMapper.toAppointment(appointment);
    }

    @Override
    public Page<AppointmentResponse> patientHistory(
            String username,
            Pageable pageable
    ) {

        User patient = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Patient not found"));

        return repo.findByPatient(patient, pageable)
                .map(ResponseMapper::toAppointment);
    }

    @Override
    public Page<AppointmentResponse> doctorAppointments(
            String username,
            Pageable pageable
    ) {

        User doctor = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Doctor not found"));

        return repo.findByDoctor(doctor, pageable)
                .map(ResponseMapper::toAppointment);
    }

    @Override
    public AppointmentResponse updateStatus(
            Long id,
            AppointmentStatusRequest r
    ) {

        Appointment appointment = repo.findById(id)
                .orElseThrow(() -> new ApiException("Appointment not found"));

        validateTransition(
                appointment.getStatus(),
                r.getStatus()
        );

        appointment.setStatus(r.getStatus());

        return ResponseMapper.toAppointment(
                repo.save(appointment)
        );
    }

    @Override
    public AppointmentResponse updateStatusByDoctor(
            Long id,
            AppointmentStatusRequest r,
            String doctorUsername
    ) {

        User doctor = userRepo.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException("Doctor not found"));

        Appointment appointment = repo.findById(id)
                .orElseThrow(() -> new ApiException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new ForbiddenException(
                    "Doctor can update only own appointments"
            );
        }

        validateTransition(
                appointment.getStatus(),
                r.getStatus()
        );

        appointment.setStatus(r.getStatus());

        return ResponseMapper.toAppointment(
                repo.save(appointment)
        );
    }

    @Override
    public Page<AppointmentResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(ResponseMapper::toAppointment);
    }

    private void validateTransition(
            AppointmentStatus current,
            AppointmentStatus next
    ) {

        if (current == next) {
            return;
        }

        boolean valid =
                (current == AppointmentStatus.PENDING &&
                        (next == AppointmentStatus.APPROVED
                                || next == AppointmentStatus.REJECTED
                                || next == AppointmentStatus.CANCELLED))
                        ||
                        (current == AppointmentStatus.APPROVED &&
                                (next == AppointmentStatus.COMPLETED
                                        || next == AppointmentStatus.CANCELLED));

        if (!valid) {
            throw new ApiException(
                    "Invalid appointment status transition from "
                            + current + " to " + next
            );
        }
    }
}