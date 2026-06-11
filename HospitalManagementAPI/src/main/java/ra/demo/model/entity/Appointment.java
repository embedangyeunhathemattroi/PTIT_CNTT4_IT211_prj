package ra.demo.model.entity;

// Import các annotation JPA
import jakarta.persistence.*;

// Import Lombok
import lombok.*;

// Enum trạng thái lịch hẹn
import ra.demo.model.enums.AppointmentStatus;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Đánh dấu đây là Entity ánh xạ với bảng trong database
@Entity

// Tự sinh getter
@Getter

// Tự sinh setter
@Setter

// Constructor rỗng
@NoArgsConstructor

// Constructor đầy đủ tham số
@AllArgsConstructor

// Builder Pattern
@Builder
public class Appointment {

    // Khóa chính
    @Id

    // Tự động tăng
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bệnh nhân đặt lịch
    // Nhiều lịch hẹn có thể thuộc một bệnh nhân
    @ManyToOne(optional = false)
    private User patient;

    // Bác sĩ khám bệnh
    // Nhiều lịch hẹn có thể thuộc một bác sĩ
    @ManyToOne(optional = false)
    private User doctor;

    // Thời gian khám
    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    // Trạng thái lịch hẹn
    // Lưu dưới dạng String trong DB
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    // Mô tả triệu chứng
    // Tối đa 1000 ký tự
    @Column(length = 1000)
    private String symptomDescription;
}