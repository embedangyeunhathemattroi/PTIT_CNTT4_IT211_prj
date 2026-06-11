package ra.demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.demo.exception.FileStorageException;
import ra.demo.service.CloudStorageService;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudStorageServiceImpl implements CloudStorageService {
    private final Cloudinary cloudinary;
    private final String folder;

    public CloudStorageServiceImpl(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret,
            @Value("${cloudinary.folder:hospital-management/medical-records}") String folder) {
        this.folder = folder;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public String uploadMedicalRecord(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto"
            ));
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null || secureUrl.toString().isBlank()) {
                throw new FileStorageException("Cloudinary did not return secure_url");
            }
            log.info("Uploaded medical record to Cloudinary: public_id={}", result.get("public_id"));
            return secureUrl.toString();
        } catch (IOException e) {
            throw new FileStorageException("Cannot upload medical record file to Cloudinary");
        } catch (RuntimeException e) {
            throw new FileStorageException("Cloudinary upload failed: " + e.getMessage());
        }
    }
}
