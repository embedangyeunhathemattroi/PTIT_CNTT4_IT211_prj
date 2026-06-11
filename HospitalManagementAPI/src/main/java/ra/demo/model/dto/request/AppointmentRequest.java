package ra.demo.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequest {
    @NotNull
    private Long doctorId;
    @Future
    private LocalDateTime appointmentTime;
    @NotBlank
    private String symptomDescription;
}
