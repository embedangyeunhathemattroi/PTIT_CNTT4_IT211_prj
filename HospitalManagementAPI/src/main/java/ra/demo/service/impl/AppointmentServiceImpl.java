package ra.demo.service.impl;

// Lombok tạo constructor
import lombok.RequiredArgsConstructor;
// Phân trang
import org.springframework.data.domain.Page;
// Pageable
import org.springframework.data.domain.Pageable;
// Service Bean
import org.springframework.stereotype.Service;
// Exception nghiệp vụ
import ra.demo.exception.ApiException;
// Exception trùng dữ liệu
import ra.demo.exception.ConflictException;
// Exception cấm truy cập
import ra.demo.exception.ForbiddenException;
// Mapper DTO
import ra.demo.mapper.ResponseMapper;
// Request tạo lịch hẹn
import ra.demo.model.dto.request.AppointmentRequest;
// Request cập nhật trạng thái
import ra.demo.model.dto.request.AppointmentStatusRequest;
// Response lịch hẹn
import ra.demo.model.dto.response.AppointmentResponse;
// Entity Appointment
import ra.demo.model.entity.Appointment;
// Entity User
import ra.demo.model.entity.User;
// Enum trạng thái
import ra.demo.model.enums.AppointmentStatus;
// Enum Role
import ra.demo.model.enums.RoleName;
// Repository Appointment
import ra.demo.repository.AppointmentRepository;
// Repository User
import ra.demo.repository.UserRepository;
// Interface Service
import ra.demo.service.AppointmentService;
// List
import java.util.List;

// Đăng ký Service
@Service
// Tạo constructor
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    // Repository Appointment
    private final AppointmentRepository repo;

    // Repository User
    private final UserRepository userRepo;

    // Tạo lịch hẹn
    public AppointmentResponse create(AppointmentRequest r, String username) {

        // Tìm bệnh nhân
        User patient = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Patient not found"));

        // Tìm bác sĩ
        User doctor = userRepo.findById(r.getDoctorId())
                .orElseThrow(() -> new ApiException("Doctor not found"));

        // Kiểm tra role doctor
        if (doctor.getRole() != RoleName.DOCTOR)
            throw new ApiException("Selected user is not doctor");

        // Kiểm tra trùng lịch khám
        if (repo.existsByDoctorAndAppointmentTimeAndStatusIn(
                doctor,
                r.getAppointmentTime(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.APPROVED)))

            throw new ConflictException("Doctor already has appointment at this time");

        // Lưu lịch hẹn
        Appointment a = repo.save(
                Appointment.builder()
                        .patient(patient) // bệnh nhân
                        .doctor(doctor) // bác sĩ
                        .appointmentTime(r.getAppointmentTime()) // thời gian khám
                        .symptomDescription(r.getSymptomDescription()) // triệu chứng
                        .status(AppointmentStatus.PENDING) // trạng thái mặc định
                        .build());

        // Trả DTO
        return ResponseMapper.toAppointment(a);
    }

    // Lịch sử khám bệnh nhân
    public Page<AppointmentResponse> patientHistory(String username, Pageable p) {

        // Tìm bệnh nhân
        User patient = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Patient not found"));

        // Trả danh sách lịch hẹn
        return repo.findByPatient(patient, p)
                .map(ResponseMapper::toAppointment);
    }

    // Danh sách lịch khám bác sĩ
    public Page<AppointmentResponse> doctorAppointments(String username, Pageable p) {

        // Tìm bác sĩ
        User doctor = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Doctor not found"));

        // Trả danh sách lịch khám
        return repo.findByDoctor(doctor, p)
                .map(ResponseMapper::toAppointment);
    }

    // Admin cập nhật trạng thái
    public AppointmentResponse updateStatus(Long id, AppointmentStatusRequest r) {

        // Tìm lịch hẹn
        Appointment a = repo.findById(id)
                .orElseThrow(() -> new ApiException("Appointment not found"));

        // Kiểm tra luồng trạng thái
        validateTransition(a.getStatus(), r.getStatus());

        // Cập nhật trạng thái
        a.setStatus(r.getStatus());

        // Lưu DB
        return ResponseMapper.toAppointment(repo.save(a));
    }

    // Doctor cập nhật trạng thái
    public AppointmentResponse updateStatusByDoctor(Long id,
                                                    AppointmentStatusRequest r,
                                                    String doctorUsername) {

        // Tìm doctor
        User doctor = userRepo.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException("Doctor not found"));

        // Tìm lịch hẹn
        Appointment a = repo.findById(id)
                .orElseThrow(() -> new ApiException("Appointment not found"));

        // Chỉ được sửa lịch của mình
        if (!a.getDoctor().getId().equals(doctor.getId())) {
            throw new ForbiddenException("Doctor can update only own appointments");
        }

        // Kiểm tra trạng thái
        validateTransition(a.getStatus(), r.getStatus());

        // Cập nhật trạng thái
        a.setStatus(r.getStatus());

        // Lưu DB
        return ResponseMapper.toAppointment(repo.save(a));
    }

    // Admin xem tất cả lịch hẹn
    public Page<AppointmentResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(ResponseMapper::toAppointment);
    }

    // Kiểm tra chuyển trạng thái
    private void validateTransition(AppointmentStatus current,
                                    AppointmentStatus next) {

        // Không đổi trạng thái
        if (current == next) return;

        // Các chuyển trạng thái hợp lệ
        boolean ok =
                (current == AppointmentStatus.PENDING &&
                        (next == AppointmentStatus.APPROVED
                                || next == AppointmentStatus.REJECTED
                                || next == AppointmentStatus.CANCELLED))
                        ||
                        (current == AppointmentStatus.APPROVED &&
                                (next == AppointmentStatus.COMPLETED
                                        || next == AppointmentStatus.CANCELLED));

        // Không hợp lệ
        if (!ok)
            throw new ApiException(
                    "Invalid appointment status transition from "
                            + current + " to " + next);
    }
}