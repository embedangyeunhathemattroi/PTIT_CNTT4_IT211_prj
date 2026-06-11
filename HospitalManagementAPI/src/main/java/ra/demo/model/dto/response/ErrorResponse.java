package ra.demo.model.dto.response;

// Import Lombok
import lombok.*;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Sinh getter
@Getter

// Sinh setter
@Setter

// Constructor rỗng
@NoArgsConstructor

// Constructor đầy đủ tham số
@AllArgsConstructor

// Builder Pattern
@Builder
public class ErrorResponse {

    // Thời gian phát sinh lỗi
    private LocalDateTime timestamp;

    // HTTP Status Code
    private int status;

    // Tên lỗi
    private String error;

    // Nội dung lỗi
    private String message;

    // Đường dẫn API bị lỗi
    private String path;
}