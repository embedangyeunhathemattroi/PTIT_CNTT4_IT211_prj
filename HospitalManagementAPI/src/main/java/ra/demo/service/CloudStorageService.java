package ra.demo.service;

// Đối tượng đại diện file upload từ client
import org.springframework.web.multipart.MultipartFile;

// Interface định nghĩa chức năng lưu trữ file trên cloud
public interface CloudStorageService {

    // Upload hồ sơ bệnh án lên dịch vụ lưu trữ (Cloudinary)
    // Nhận file từ client
    // Trả về URL của file sau khi upload thành công
    String uploadMedicalRecord(MultipartFile file);
}