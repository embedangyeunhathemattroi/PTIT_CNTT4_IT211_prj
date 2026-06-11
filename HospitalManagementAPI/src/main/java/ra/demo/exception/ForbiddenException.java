package ra.demo.exception;

// Custom Exception dùng cho các trường hợp không đủ quyền thực hiện hành động
// Kế thừa RuntimeException nên là Unchecked Exception
public class ForbiddenException extends RuntimeException {

    // Constructor nhận thông báo lỗi
    public ForbiddenException(String message) {

        // Gọi constructor của lớp cha RuntimeException
        // để lưu nội dung message
        super(message);
    }
}

//
//ForbiddenException là Exception tự định nghĩa dùng để xử lý các trường hợp:
//
//Người dùng không có quyền thao tác
//Bác sĩ truy cập dữ liệu không thuộc quyền quản lý
//Bệnh nhân xem hồ sơ của người khác
//User thực hiện hành động bị cấm theo nghiệp vụ