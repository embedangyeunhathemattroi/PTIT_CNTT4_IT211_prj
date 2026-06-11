package ra.demo.model.dto.response;

// Import Lombok
import lombok.*;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Sinh getter
@Getter

// Sinh setter
@Setter

// Constructor rỗng
@NoArgsConstructor

// Constructor đầy đủ tham số
@AllArgsConstructor

// Builder Pattern
@Builder
public class MedicalRecordResponse {

    // ID hồ sơ bệnh án
    private Long id;

    // ID lịch hẹn
    private Long appointmentId;

    // ID bệnh nhân
    private Long patientId;

    // Tên bệnh nhân
    private String patientName;

    // ID bác sĩ
    private Long doctorId;

    // Tên bác sĩ
    private String doctorName;

    // Kết quả chẩn đoán
    private String diagnosis;

    // Đường dẫn file bệnh án
    private String fileUrl;

    // Thời gian tạo hồ sơ
    private LocalDateTime createdAt;
}