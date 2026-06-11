package ra.demo.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ra.demo.exception.ApiException;
import ra.demo.exception.ConflictException;
import ra.demo.exception.FileStorageException;
import ra.demo.exception.ForbiddenException;
import ra.demo.model.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiAdviceController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage()).collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, msg, req);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(RuntimeException e, HttpServletRequest r) {
        return error(HttpStatus.CONFLICT, e.getMessage(), r);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> api(RuntimeException e, HttpServletRequest r) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage(), r);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbiddenBusiness(RuntimeException e, HttpServletRequest r) {
        return error(HttpStatus.FORBIDDEN, e.getMessage(), r);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> storage(RuntimeException e, HttpServletRequest r) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), r);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentials(RuntimeException e, HttpServletRequest r) {
        return error(HttpStatus.UNAUTHORIZED, "Username or password is incorrect", r);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> forbidden(RuntimeException e, HttpServletRequest r) {
        return error(HttpStatus.FORBIDDEN, "Access denied", r);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> method(HttpRequestMethodNotSupportedException e, HttpServletRequest r) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), r);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> other(Exception e, HttpServletRequest r) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), r);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus s, String m, HttpServletRequest r) {
        return ResponseEntity.status(s).body(ErrorResponse.builder().timestamp(LocalDateTime.now()).status(s.value())
                .error(s.getReasonPhrase()).message(m).path(r.getRequestURI()).build());
    }
}
