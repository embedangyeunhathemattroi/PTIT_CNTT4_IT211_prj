package ra.demo.model.entity;

// Import JPA
import jakarta.persistence.*;

// Import Lombok
import lombok.*;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Entity hồ sơ bệnh án
@Entity

// Lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    // ID hồ sơ bệnh án
    @Id

    // Tự động tăng
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Một lịch hẹn chỉ có một hồ sơ bệnh án
    @OneToOne(optional = false)
    private Appointment appointment;

    // Bác sĩ tạo hồ sơ
    @ManyToOne(optional = false)
    private User doctor;

    // Bệnh nhân sở hữu hồ sơ
    @ManyToOne(optional = false)
    private User patient;

    // Kết quả chẩn đoán
    @Column(length = 2000)
    private String diagnosis;

    // Đường dẫn file PDF hoặc tài liệu
    private String fileUrl;

    // Thời gian tạo hồ sơ
    private LocalDateTime createdAt;
}