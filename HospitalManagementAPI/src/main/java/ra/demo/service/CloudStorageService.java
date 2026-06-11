package ra.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudStorageService {
    String uploadMedicalRecord(MultipartFile file);
}
