package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.demo.exception.ApiException;
import ra.demo.mapper.ResponseMapper;
import ra.demo.model.dto.request.MedicalRecordRequest;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.MedicalRecord;
import ra.demo.model.entity.User;
import ra.demo.model.enums.AppointmentStatus;
import ra.demo.repository.AppointmentRepository;
import ra.demo.repository.MedicalRecordRepository;
import ra.demo.repository.UserRepository;
import ra.demo.service.CloudStorageService;
import ra.demo.service.MedicalRecordService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository repo;
    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;
    private final CloudStorageService cloudStorageService;
    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxSize;

    public MedicalRecordResponse upload(MedicalRecordRequest r, MultipartFile file, String doctorUsername) {
        validateFile(file);
        Appointment a = appointmentRepo.findById(r.getAppointmentId())
                .orElseThrow(() -> new ApiException("Appointment not found"));
        User doctor = userRepo.findByUsername(doctorUsername).orElseThrow(() -> new ApiException("Doctor not found"));
        if (!a.getDoctor().getId().equals(doctor.getId()))
            throw new ApiException("Doctor can upload record only for own appointment");
        if (a.getStatus() != AppointmentStatus.COMPLETED && a.getStatus() != AppointmentStatus.APPROVED)
            throw new ApiException("Appointment must be approved or completed before uploading record");
        if (repo.existsByAppointment(a)) throw new ApiException("Medical record already exists for this appointment");
        String fileUrl = cloudStorageService.uploadMedicalRecord(file);
        MedicalRecord saved = repo.save(MedicalRecord.builder().appointment(a).doctor(doctor).patient(a.getPatient())
                .diagnosis(r.getDiagnosis()).fileUrl(fileUrl).createdAt(LocalDateTime.now()).build());
        return ResponseMapper.toRecord(saved);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new ApiException("File is required");
        if (file.getSize() > 10 * 1024 * 1024) throw new ApiException("File exceeds 10MB limit");
        String ct = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase();
        if (!(ct.contains("pdf") || ct.contains("image") || ct.contains("octet-stream")))
            throw new ApiException("Only image or PDF medical records are allowed");
    }

    public Page<MedicalRecordResponse> patientRecords(String username, Pageable pageable) {
        User patient = userRepo.findByUsername(username).orElseThrow(() -> new ApiException("Patient not found"));
        return repo.findByPatient(patient, pageable).map(ResponseMapper::toRecord);
    }

    public Page<MedicalRecordResponse> doctorRecords(String username, Pageable pageable) {
        User doctor = userRepo.findByUsername(username).orElseThrow(() -> new ApiException("Doctor not found"));
        return repo.findByDoctor(doctor, pageable).map(ResponseMapper::toRecord);
    }

    public Page<MedicalRecordResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(ResponseMapper::toRecord);
    }

    public MedicalRecordResponse findById(Long id) {
        return repo.findById(id).map(ResponseMapper::toRecord)
                .orElseThrow(() -> new ApiException("Medical record not found"));
    }
}
