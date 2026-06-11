package ra.demo.mapper;

// Import các DTO dùng để trả dữ liệu cho client
//ResponseMapper là lớp chuyên dùng để:
//
//Entity  --->  Response DTO
//
//Giúp:
//
//Không trả trực tiếp Entity cho client.
//Ẩn các trường nhạy cảm.
//Chuẩn hóa dữ liệu trả về.
//Giảm code lặp trong Service.
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.model.dto.response.UserResponse;

// Import các Entity lấy dữ liệu từ database
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.MedicalRecord;
import ra.demo.model.entity.User;

// Lớp Mapper dùng để chuyển đổi Entity -> Response DTO
public class ResponseMapper {

    // Chuyển đối tượng User thành UserResponse
    public static UserResponse toUser(User u) {

        // Sử dụng Builder Pattern để tạo UserResponse
        return UserResponse.builder()

                // Gán id
                .id(u.getId())

                // Gán username
                .username(u.getUsername())

                // Gán email
                .email(u.getEmail())

                // Gán họ tên
                .fullName(u.getFullName())

                // Gán role
                .role(u.getRole())

                // Gán trạng thái hoạt động
                .active(u.isActive())

                // Hoàn thành đối tượng UserResponse
                .build();
    }

    // Chuyển Appointment Entity thành AppointmentResponse
    public static AppointmentResponse toAppointment(Appointment a) {

        // Tạo AppointmentResponse bằng Builder
        return AppointmentResponse.builder()

                // ID lịch hẹn
                .id(a.getId())

                // ID bệnh nhân
                .patientId(a.getPatient().getId())

                // Tên bệnh nhân
                .patientName(a.getPatient().getFullName())

                // ID bác sĩ
                .doctorId(a.getDoctor().getId())

                // Tên bác sĩ
                .doctorName(a.getDoctor().getFullName())

                // Thời gian khám
                .appointmentTime(a.getAppointmentTime())

                // Trạng thái lịch hẹn
                .status(a.getStatus())

                // Triệu chứng bệnh nhân mô tả
                .symptomDescription(a.getSymptomDescription())

                // Hoàn thành đối tượng
                .build();
    }

    // Chuyển MedicalRecord Entity thành MedicalRecordResponse
    public static MedicalRecordResponse toRecord(MedicalRecord r) {

        // Tạo MedicalRecordResponse bằng Builder
        return MedicalRecordResponse.builder()

                // ID hồ sơ bệnh án
                .id(r.getId())

                // ID lịch hẹn liên quan
                .appointmentId(r.getAppointment().getId())

                // ID bệnh nhân
                .patientId(r.getPatient().getId())

                // Tên bệnh nhân
                .patientName(r.getPatient().getFullName())

                // ID bác sĩ
                .doctorId(r.getDoctor().getId())

                // Tên bác sĩ
                .doctorName(r.getDoctor().getFullName())

                // Chẩn đoán bệnh
                .diagnosis(r.getDiagnosis())

                // Đường dẫn file bệnh án
                .fileUrl(r.getFileUrl())

                // Thời gian tạo hồ sơ
                .createdAt(r.getCreatedAt())

                // Hoàn thành đối tượng
                .build();
    }
}