package ra.demo.exception;

// Custom Exception dùng cho các lỗi liên quan đến lưu trữ file
// Kế thừa RuntimeException nên là Unchecked Exception,FileStorageException là Exception tự định nghĩa để xử lý các lỗi liên quan đến:
//
//Upload file
//Lưu file vào server
//Đọc file
//Xóa file
//Tạo thư mục lưu trữ
//Truy cập file không tồn tại
public class FileStorageException extends RuntimeException {

    // Constructor nhận thông báo lỗi
    public FileStorageException(String message) {

        // Gọi constructor của lớp cha RuntimeException
        // để lưu nội dung message
        super(message);
    }
}