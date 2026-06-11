package ra.demo.repository;

// Import lớp Page dùng cho phân trang
import org.springframework.data.domain.Page;

// Import Pageable chứa thông tin phân trang và sắp xếp
import org.springframework.data.domain.Pageable;

// JpaRepository cung cấp sẵn các hàm CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Entity Appointment
import ra.demo.model.entity.Appointment;

// Entity User
import ra.demo.model.entity.User;

// Enum trạng thái lịch hẹn
import ra.demo.model.enums.AppointmentStatus;

// Kiểu dữ liệu ngày giờ
import java.time.LocalDateTime;

// Collection dùng cho danh sách trạng thái
import java.util.Collection;

// Repository thao tác với bảng Appointment
// Long là kiểu dữ liệu của khóa chính
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Kiểm tra xem bác sĩ đã có lịch khám tại thời điểm này hay chưa
    // Đồng thời chỉ kiểm tra với các trạng thái được truyền vào
    boolean existsByDoctorAndAppointmentTimeAndStatusIn(
            User doctor,
            LocalDateTime appointmentTime,
            Collection<AppointmentStatus> statuses);

    // Lấy danh sách lịch hẹn của một bệnh nhân
    // Có hỗ trợ phân trang
    Page<Appointment> findByPatient(
            User patient,
            Pageable pageable);

    // Lấy danh sách lịch hẹn của một bác sĩ
    // Có hỗ trợ phân trang
    Page<Appointment> findByDoctor(
            User doctor,
            Pageable pageable);
}