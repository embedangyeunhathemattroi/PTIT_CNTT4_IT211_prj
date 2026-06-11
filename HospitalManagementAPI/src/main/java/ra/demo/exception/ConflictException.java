package ra.demo.exception;

// Exception tự định nghĩa dùng cho các trường hợp xung đột dữ liệu
// Kế thừa RuntimeException nên là Unchecked Exception,ConflictException là Custom Exception dùng để biểu thị các lỗi xung đột dữ liệu (Conflict) trong hệ thống.
public class ConflictException extends RuntimeException {

    // Constructor nhận thông báo lỗi
    public ConflictException(String message) {

        // Gọi constructor của lớp cha RuntimeException
        // để lưu nội dung message
        super(message);
    }
}