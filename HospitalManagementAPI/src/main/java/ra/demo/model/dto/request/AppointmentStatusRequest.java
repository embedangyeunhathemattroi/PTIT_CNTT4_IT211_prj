package ra.demo.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ra.demo.model.enums.AppointmentStatus;

@Getter
@Setter
public class AppointmentStatusRequest {
    @NotNull
    private AppointmentStatus status;
}
