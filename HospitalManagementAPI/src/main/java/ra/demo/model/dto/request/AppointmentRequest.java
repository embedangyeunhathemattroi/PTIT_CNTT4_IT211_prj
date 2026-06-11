package ra.demo.model.dto.request;

// Validation: giá trị ngày giờ phải ở tương lai
import jakarta.validation.constraints.Future;

// Validation: không được null, rỗng hoặc chỉ chứa khoảng trắng
import jakarta.validation.constraints.NotBlank;

// Validation: không được null
import jakarta.validation.constraints.NotNull;

// Lombok tự sinh getter
import lombok.Getter;

// Lombok tự sinh setter
import lombok.Setter;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Tự động sinh getter cho các field
@Getter

// Tự động sinh setter cho các field
@Setter
public class AppointmentRequest {

    // ID bác sĩ được chọn
    // Không được để null
    @NotNull
    private Long doctorId;

    // Thời gian khám
    // Phải là thời điểm trong tương lai
    @Future
    private LocalDateTime appointmentTime;

    // Mô tả triệu chứng bệnh
    // Không được để trống
    @NotBlank
    private String symptomDescription;
}