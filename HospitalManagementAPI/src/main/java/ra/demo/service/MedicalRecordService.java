package ra.demo.service;

// Hỗ trợ phân trang dữ liệu
import org.springframework.data.domain.Page;

// Chứa thông tin phân trang như page, size, sort
import org.springframework.data.domain.Pageable;

// Đại diện file upload từ request multipart/form-data
import org.springframework.web.multipart.MultipartFile;

// DTO nhận dữ liệu tạo hồ sơ bệnh án
import ra.demo.model.dto.request.MedicalRecordRequest;

// DTO trả về dữ liệu hồ sơ bệnh án
import ra.demo.model.dto.response.MedicalRecordResponse;

// Interface định nghĩa các chức năng quản lý hồ sơ bệnh án
public interface MedicalRecordService {

    // Upload hồ sơ bệnh án mới
    // request chứa appointmentId và diagnosis
    // file là PDF hoặc ảnh bệnh án
    // doctorUsername là username bác sĩ đang đăng nhập
    MedicalRecordResponse upload(
            MedicalRecordRequest request,
            MultipartFile file,
            String doctorUsername
    );

    // Lấy danh sách hồ sơ bệnh án của bệnh nhân
    // username là tài khoản bệnh nhân
    // pageable dùng cho phân trang
    Page<MedicalRecordResponse> patientRecords(
            String username,
            Pageable pageable
    );

    // Lấy danh sách hồ sơ bệnh án của bác sĩ
    // username là tài khoản bác sĩ
    // pageable dùng cho phân trang
    Page<MedicalRecordResponse> doctorRecords(
            String username,
            Pageable pageable
    );

    // Lấy toàn bộ hồ sơ bệnh án
    // Chức năng dành cho Admin
    Page<MedicalRecordResponse> findAll(
            Pageable pageable
    );

    // Lấy chi tiết hồ sơ bệnh án theo id
    MedicalRecordResponse findById(
            Long id
    );
}