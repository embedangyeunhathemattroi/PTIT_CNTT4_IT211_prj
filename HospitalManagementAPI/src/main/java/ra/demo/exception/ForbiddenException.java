package ra.demo.exception;

// Custom Exception dùng cho các trường hợp không đủ quyền thực hiện hành động:Bác sĩ truy cập dữ liệu không thuộc quyền quản lý,Bệnh nhân xem hồ sơ của người khác,...
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}