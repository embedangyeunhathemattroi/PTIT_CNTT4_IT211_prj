package ra.demo.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalRecordRequest {
    @NotNull
    private Long appointmentId;
    @NotBlank
    private String diagnosis;
}
