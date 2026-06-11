package ra.demo.advice;

// Lấy thông tin request hiện tại
import jakarta.servlet.http.HttpServletRequest;

// Chứa các mã trạng thái HTTP (200, 400, 404, 500...)
import org.springframework.http.HttpStatus;

// Dùng để trả về response có status + body
import org.springframework.http.ResponseEntity;

// Exception khi đăng nhập sai username hoặc password
import org.springframework.security.authentication.BadCredentialsException;

// Exception khi bị từ chối quyền truy cập
import org.springframework.security.authorization.AuthorizationDeniedException;

// Exception khi gọi sai HTTP Method (GET, POST,...)
import org.springframework.web.HttpRequestMethodNotSupportedException;

// Exception phát sinh khi validate dữ liệu bằng @Valid
import org.springframework.web.bind.MethodArgumentNotValidException;

// Đánh dấu đây là class xử lý exception toàn cục
import org.springframework.web.bind.annotation.ExceptionHandler;

// Kết hợp @ControllerAdvice + @ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Exception tự định nghĩa của hệ thống
import ra.demo.exception.ApiException;

// Exception dữ liệu bị trùng
import ra.demo.exception.ConflictException;

// Exception liên quan upload/lưu file
import ra.demo.exception.FileStorageException;

// Exception không đủ quyền thao tác
import ra.demo.exception.ForbiddenException;

// DTO dùng để trả về lỗi
import ra.demo.model.dto.response.ErrorResponse;

// Lấy thời gian hiện tại
import java.time.LocalDateTime;

// Hỗ trợ xử lý Stream
import java.util.stream.Collectors;

// Đánh dấu đây là bộ xử lý exception dùng cho toàn bộ REST API
@RestControllerAdvice
public class ApiAdviceController {

    // Bắt lỗi validation từ @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(
            MethodArgumentNotValidException e,
            HttpServletRequest req
    ) {

        // Lấy danh sách lỗi validate
        String msg = e.getBindingResult()

                // Lấy các field bị lỗi
                .getFieldErrors()

                // Chuyển thành Stream để xử lý
                .stream()

                // Format lỗi theo dạng:
                // username: must not be blank
                .map(x -> x.getField() + ": " + x.getDefaultMessage())

                // Nối các lỗi thành 1 chuỗi
                .collect(Collectors.joining("; "));

        // Trả về HTTP 400
        return error(HttpStatus.BAD_REQUEST, msg, req);
    }

    // Bắt lỗi dữ liệu bị trùng
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(
            RuntimeException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 409
        return error(HttpStatus.CONFLICT, e.getMessage(), r);
    }

    // Bắt lỗi nghiệp vụ chung
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> api(
            RuntimeException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 400
        return error(HttpStatus.BAD_REQUEST, e.getMessage(), r);
    }

    // Bắt lỗi không đủ quyền theo nghiệp vụ
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbiddenBusiness(
            RuntimeException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 403
        return error(HttpStatus.FORBIDDEN, e.getMessage(), r);
    }

    // Bắt lỗi lưu file
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> storage(
            RuntimeException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 503
        return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), r);
    }

    // Bắt lỗi đăng nhập sai
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentials(
            RuntimeException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 401
        return error(
                HttpStatus.UNAUTHORIZED,
                "Username or password is incorrect",
                r
        );
    }

    // Bắt lỗi phân quyền của Spring Security
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> forbidden(
            RuntimeException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 403
        return error(
                HttpStatus.FORBIDDEN,
                "Access denied",
                r
        );
    }

    // Bắt lỗi gọi sai HTTP Method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> method(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 405
        return error(
                HttpStatus.METHOD_NOT_ALLOWED,
                e.getMessage(),
                r
        );
    }

    // Bắt tất cả exception còn lại
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> other(
            Exception e,
            HttpServletRequest r
    ) {

        // Trả về HTTP 500
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                r
        );
    }

    // Hàm tạo ErrorResponse dùng chung
    private ResponseEntity<ErrorResponse> error(
            HttpStatus s,      // HTTP Status
            String m,          // Nội dung lỗi
            HttpServletRequest r // Request hiện tại
    ) {

        // Tạo response với status tương ứng
        return ResponseEntity.status(s)

                // Gắn body vào response
                .body(

                        // Tạo đối tượng ErrorResponse bằng Builder
                        ErrorResponse.builder()

                                // Thời gian xảy ra lỗi
                                .timestamp(LocalDateTime.now())

                                // Mã HTTP Status
                                .status(s.value())

                                // Tên trạng thái
                                .error(s.getReasonPhrase())

                                // Nội dung lỗi
                                .message(m)

                                // API gây lỗi
                                .path(r.getRequestURI())

                                // Hoàn thành object
                                .build()
                );
    }
}