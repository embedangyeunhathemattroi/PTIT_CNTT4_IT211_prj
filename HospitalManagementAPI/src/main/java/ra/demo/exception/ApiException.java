package ra.demo.exception;

// Exception tự định nghĩa của hệ thống
// Kế thừa RuntimeException nên là Unchecked Exception
//ApiException là một Custom Exception (ngoại lệ tự định nghĩa).
//
//Class này được tạo ra để xử lý các lỗi nghiệp vụ trong hệ thống thay vì dùng trực tiếp RuntimeException.
public class ApiException extends RuntimeException {

    // Constructor nhận thông báo lỗi
    public ApiException(String message) {

        // Gọi constructor của RuntimeException
        // để lưu nội dung message
        super(message);
    }
}