package ra.demo.model.dto.response;

// Import tất cả annotation Lombok
import lombok.*;

// Tự động sinh Getter cho tất cả field
@Getter

// Tự động sinh Setter cho tất cả field
@Setter

// Tự động sinh constructor không tham số
@NoArgsConstructor

// Tự động sinh constructor đầy đủ tham số
@AllArgsConstructor

// Hỗ trợ Builder Pattern
@Builder
public class ApiDataResponse<T> {

    // Trạng thái thành công hay thất bại
    private boolean success;

    // Thông báo trả về cho client
    private String message;

    // Dữ liệu trả về (Generic)
    private T data;

    // HTTP Status Code
    private int status;

    // Hàm tiện ích tạo response thành công
    public static <T> ApiDataResponse<T> success(
            String message,
            T data,
            int status) {

        return ApiDataResponse.<T>builder()

                // Đánh dấu thành công
                .success(true)

                // Gán message
                .message(message)

                // Gán dữ liệu
                .data(data)

                // Gán status
                .status(status)

                // Trả đối tượng
                .build();
    }
}