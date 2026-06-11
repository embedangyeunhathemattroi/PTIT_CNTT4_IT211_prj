package ra.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ra.demo.model.dto.request.MedicalRecordRequest;
import ra.demo.model.dto.response.MedicalRecordResponse;

public interface MedicalRecordService {
    MedicalRecordResponse upload(MedicalRecordRequest request, MultipartFile file, String doctorUsername);

    Page<MedicalRecordResponse> patientRecords(String username, Pageable pageable);

    Page<MedicalRecordResponse> doctorRecords(String username, Pageable pageable);

    Page<MedicalRecordResponse> findAll(Pageable pageable);

    MedicalRecordResponse findById(Long id);
}
