package ra.demo.model.dto.request;

// Validation không được null
import jakarta.validation.constraints.NotNull;

// Lombok getter
import lombok.Getter;

// Lombok setter
import lombok.Setter;

// Enum trạng thái lịch hẹn
import ra.demo.model.enums.AppointmentStatus;

// Sinh getter
@Getter

// Sinh setter
@Setter
public class AppointmentStatusRequest {

    // Trạng thái mới của lịch hẹn
    @NotNull
    private AppointmentStatus status;
}