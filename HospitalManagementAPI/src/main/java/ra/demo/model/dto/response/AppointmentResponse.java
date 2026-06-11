package ra.demo.model.dto.response;

// Import Lombok
import lombok.*;

// Enum trạng thái lịch hẹn
import ra.demo.model.enums.AppointmentStatus;

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
public class AppointmentResponse {

    // ID lịch hẹn
    private Long id;

    // ID bệnh nhân
    private Long patientId;

    // Tên bệnh nhân
    private String patientName;

    // ID bác sĩ
    private Long doctorId;

    // Tên bác sĩ
    private String doctorName;

    // Thời gian khám
    private LocalDateTime appointmentTime;

    // Trạng thái lịch hẹn
    private AppointmentStatus status;

    // Mô tả triệu chứng
    private String symptomDescription;
}