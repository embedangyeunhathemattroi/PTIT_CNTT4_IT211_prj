package ra.demo.repository;

// Import lớp Page dùng cho phân trang kết quả
import org.springframework.data.domain.Page;

// Import Pageable chứa thông tin phân trang và sắp xếp
import org.springframework.data.domain.Pageable;

// JpaRepository cung cấp sẵn các phương thức CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Entity Appointment
import ra.demo.model.entity.Appointment;

// Entity MedicalRecord
import ra.demo.model.entity.MedicalRecord;

// Entity User
import ra.demo.model.entity.User;

// Repository thao tác với bảng MedicalRecord
// MedicalRecord là Entity
// Long là kiểu dữ liệu khóa chính
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Lấy danh sách hồ sơ bệnh án của một bệnh nhân
    // Có hỗ trợ phân trang
    Page<MedicalRecord> findByPatient(
            User patient,
            Pageable pageable);

    // Lấy danh sách hồ sơ bệnh án do một bác sĩ tạo
    // Có hỗ trợ phân trang
    Page<MedicalRecord> findByDoctor(
            User doctor,
            Pageable pageable);

    // Kiểm tra lịch hẹn đã có hồ sơ bệnh án hay chưa
    // Trả về true nếu đã tồn tại
    // Trả về false nếu chưa tồn tại
    boolean existsByAppointment(
            Appointment appointment);
}