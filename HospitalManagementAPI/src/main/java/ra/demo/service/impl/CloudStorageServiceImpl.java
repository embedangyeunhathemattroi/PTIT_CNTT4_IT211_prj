package ra.demo.service.impl;

// Cloudinary SDK
import com.cloudinary.Cloudinary;
// Utility tạo Map cho Cloudinary
import com.cloudinary.utils.ObjectUtils;
// Logger Lombok
import lombok.extern.slf4j.Slf4j;
// Đọc giá trị từ application.properties
import org.springframework.beans.factory.annotation.Value;
// Đăng ký Service Bean
import org.springframework.stereotype.Service;
// File upload từ client
import org.springframework.web.multipart.MultipartFile;
// Exception upload file
import ra.demo.exception.FileStorageException;
// Interface CloudStorageService
import ra.demo.service.CloudStorageService;

import java.io.IOException;
import java.util.Map;

// Đăng ký Service
@Service
// Tạo logger log
@Slf4j
public class CloudStorageServiceImpl implements CloudStorageService {

    // Đối tượng Cloudinary
    private final Cloudinary cloudinary;

    // Thư mục lưu file trên Cloudinary
    private final String folder;

    // Constructor khởi tạo Cloudinary
    public CloudStorageServiceImpl(

            // Cloud name từ config
            @Value("${cloudinary.cloud-name:}") String cloudName,

            // API key từ config
            @Value("${cloudinary.api-key:}") String apiKey,

            // API secret từ config
            @Value("${cloudinary.api-secret:}") String apiSecret,

            // Folder mặc định
            @Value("${cloudinary.folder:hospital-management/medical-records}")
            String folder) {

        // Gán folder
        this.folder = folder;

        // Khởi tạo Cloudinary
        this.cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
                        "secure", true
                ));
    }

    // Upload hồ sơ bệnh án
    @Override
    public String uploadMedicalRecord(MultipartFile file) {

        try {

            // Upload file lên Cloudinary
            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", folder,
                                    "resource_type", "auto"
                            ));

            // Lấy URL bảo mật
            Object secureUrl = result.get("secure_url");

            // Kiểm tra URL trả về
            if (secureUrl == null || secureUrl.toString().isBlank()) {
                throw new FileStorageException(
                        "Cloudinary did not return secure_url"
                );
            }

            // Ghi log upload thành công
            log.info(
                    "Uploaded medical record to Cloudinary: public_id={}",
                    result.get("public_id")
            );

            // Trả về URL file
            return secureUrl.toString();

        } catch (IOException e) {

            // Lỗi đọc file
            throw new FileStorageException(
                    "Cannot upload medical record file to Cloudinary"
            );

        } catch (RuntimeException e) {

            // Lỗi Cloudinary
            throw new FileStorageException(
                    "Cloudinary upload failed: " + e.getMessage()
            );
        }
    }
}