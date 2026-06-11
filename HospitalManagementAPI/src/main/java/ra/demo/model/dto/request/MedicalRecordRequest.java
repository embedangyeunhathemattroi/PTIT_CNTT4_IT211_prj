package ra.demo.model.dto.request;

// Validation
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Lombok
import lombok.Getter;
import lombok.Setter;

// Sinh getter
@Getter

// Sinh setter
@Setter
public class MedicalRecordRequest {

    // ID lịch hẹn tương ứng
    @NotNull
    private Long appointmentId;

    // Nội dung chẩn đoán
    @NotBlank
    private String diagnosis;
}