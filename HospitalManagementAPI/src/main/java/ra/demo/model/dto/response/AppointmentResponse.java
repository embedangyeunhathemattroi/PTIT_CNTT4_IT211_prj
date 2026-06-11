package ra.demo.model.dto.response;

import lombok.*;
import ra.demo.model.enums.AppointmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String symptomDescription;
}
