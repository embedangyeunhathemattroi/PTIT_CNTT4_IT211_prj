package ra.demo.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String diagnosis;
    private String fileUrl;
    private LocalDateTime createdAt;
}
