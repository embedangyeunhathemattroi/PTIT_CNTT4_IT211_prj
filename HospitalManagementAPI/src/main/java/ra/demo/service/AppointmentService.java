package ra.demo.service;

// Hỗ trợ phân trang dữ liệu
import org.springframework.data.domain.Page;

// Thông tin phân trang (page, size, sort)
import org.springframework.data.domain.Pageable;

// DTO tạo lịch hẹn
import ra.demo.model.dto.request.AppointmentRequest;

// DTO cập nhật trạng thái lịch hẹn
import ra.demo.model.dto.request.AppointmentStatusRequest;

// DTO trả về lịch hẹn
import ra.demo.model.dto.response.AppointmentResponse;

// Interface định nghĩa các chức năng quản lý lịch hẹn
public interface AppointmentService {

    // Bệnh nhân tạo lịch khám
    AppointmentResponse create(
            AppointmentRequest request,
            String username
    );

    // Xem lịch sử đặt lịch của bệnh nhân
    Page<AppointmentResponse> patientHistory(
            String username,
            Pageable pageable
    );

    // Xem danh sách lịch khám của bác sĩ
    Page<AppointmentResponse> doctorAppointments(
            String username,
            Pageable pageable
    );

    // Admin cập nhật trạng thái lịch hẹn
    AppointmentResponse updateStatus(
            Long id,
            AppointmentStatusRequest request
    );

    // Doctor cập nhật trạng thái lịch hẹn của mình
    AppointmentResponse updateStatusByDoctor(
            Long id,
            AppointmentStatusRequest request,
            String doctorUsername
    );

    // Admin xem tất cả lịch hẹn
    Page<AppointmentResponse> findAll(
            Pageable pageable
    );
}