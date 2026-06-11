package ra.demo.service.impl;

// Lombok tạo constructor
import lombok.RequiredArgsConstructor;
// Inject giá trị từ properties
import org.springframework.beans.factory.annotation.Value;
// Phân trang
import org.springframework.data.domain.Page;
// Pageable
import org.springframework.data.domain.Pageable;
// Service Bean
import org.springframework.stereotype.Service;
// File upload
import org.springframework.web.multipart.MultipartFile;
// Exception nghiệp vụ
import ra.demo.exception.ApiException;
// Mapper Entity -> DTO
import ra.demo.mapper.ResponseMapper;
// Request upload bệnh án
import ra.demo.model.dto.request.MedicalRecordRequest;
// Response bệnh án
import ra.demo.model.dto.response.MedicalRecordResponse;
// Entity Appointment
import ra.demo.model.entity.Appointment;
// Entity MedicalRecord
import ra.demo.model.entity.MedicalRecord;
// Entity User
import ra.demo.model.entity.User;
// Enum trạng thái lịch hẹn
import ra.demo.model.enums.AppointmentStatus;
// Repository Appointment
import ra.demo.repository.AppointmentRepository;
// Repository MedicalRecord
import ra.demo.repository.MedicalRecordRepository;
// Repository User
import ra.demo.repository.UserRepository;
// Service upload file
import ra.demo.service.CloudStorageService;
// Interface service
import ra.demo.service.MedicalRecordService;

import java.time.LocalDateTime;
import java.util.Optional;

// Đăng ký Service
@Service
// Tạo constructor
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    // Repository bệnh án
    private final MedicalRecordRepository repo;

    // Repository lịch hẹn
    private final AppointmentRepository appointmentRepo;

    // Repository user
    private final UserRepository userRepo;

    // Service upload Cloudinary
    private final CloudStorageService cloudStorageService;

    // Kích thước file tối đa
    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxSize;

    // Upload hồ sơ bệnh án
    public MedicalRecordResponse upload(
            MedicalRecordRequest r,
            MultipartFile file,
            String doctorUsername) {

        // Kiểm tra file
        validateFile(file);

        // Tìm lịch hẹn
        Appointment a = appointmentRepo.findById(r.getAppointmentId())
                .orElseThrow(() -> new ApiException("Appointment not found"));

        // Tìm bác sĩ
        User doctor = userRepo.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException("Doctor not found"));

        // Chỉ được upload cho lịch của mình
        if (!a.getDoctor().getId().equals(doctor.getId()))
            throw new ApiException(
                    "Doctor can upload record only for own appointment");

        // Lịch hẹn phải APPROVED hoặc COMPLETED
        if (a.getStatus() != AppointmentStatus.COMPLETED
                && a.getStatus() != AppointmentStatus.APPROVED)

            throw new ApiException(
                    "Appointment must be approved or completed before uploading record");

        // Kiểm tra đã có bệnh án chưa
        if (repo.existsByAppointment(a))
            throw new ApiException(
                    "Medical record already exists for this appointment");

        // Upload file lên Cloudinary
        String fileUrl =
                cloudStorageService.uploadMedicalRecord(file);

        // Lưu bệnh án
        MedicalRecord saved = repo.save(
                MedicalRecord.builder()
                        .appointment(a) // lịch hẹn
                        .doctor(doctor) // bác sĩ
                        .patient(a.getPatient()) // bệnh nhân
                        .diagnosis(r.getDiagnosis()) // chẩn đoán
                        .fileUrl(fileUrl) // link file
                        .createdAt(LocalDateTime.now()) // thời gian tạo
                        .build());

        // Trả DTO
        return ResponseMapper.toRecord(saved);
    }

    // Kiểm tra file upload
    private void validateFile(MultipartFile file) {

        // File rỗng
        if (file == null || file.isEmpty())
            throw new ApiException("File is required");

        // Quá 10MB
        if (file.getSize() > 10 * 1024 * 1024)
            throw new ApiException("File exceeds 10MB limit");

        // Lấy content type
        String ct = Optional.ofNullable(file.getContentType())
                .orElse("")
                .toLowerCase();

        // Chỉ cho phép PDF hoặc ảnh
        if (!(ct.contains("pdf")
                || ct.contains("image")
                || ct.contains("octet-stream")))

            throw new ApiException(
                    "Only image or PDF medical records are allowed");
    }

    // Danh sách bệnh án của bệnh nhân
    public Page<MedicalRecordResponse> patientRecords(
            String username,
            Pageable pageable) {

        // Tìm bệnh nhân
        User patient = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Patient not found"));

        // Trả danh sách
        return repo.findByPatient(patient, pageable)
                .map(ResponseMapper::toRecord);
    }

    // Danh sách bệnh án của bác sĩ
    public Page<MedicalRecordResponse> doctorRecords(
            String username,
            Pageable pageable) {

        // Tìm bác sĩ
        User doctor = userRepo.findByUsername(username)
                .orElseThrow(() -> new ApiException("Doctor not found"));

        // Trả danh sách
        return repo.findByDoctor(doctor, pageable)
                .map(ResponseMapper::toRecord);
    }

    // Admin xem tất cả bệnh án
    public Page<MedicalRecordResponse> findAll(
            Pageable pageable) {

        return repo.findAll(pageable)
                .map(ResponseMapper::toRecord);
    }

    // Tìm bệnh án theo id
    public MedicalRecordResponse findById(Long id) {

        return repo.findById(id)
                .map(ResponseMapper::toRecord)
                .orElseThrow(() ->
                        new ApiException("Medical record not found"));
    }
}