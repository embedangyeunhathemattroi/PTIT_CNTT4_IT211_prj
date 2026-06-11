package ra.demo.mapper;

import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.MedicalRecord;
import ra.demo.model.entity.User;

public class ResponseMapper {
    public static UserResponse toUser(User u) {
        return UserResponse.builder().id(u.getId()).username(u.getUsername()).email(u.getEmail())
                .fullName(u.getFullName()).role(u.getRole()).active(u.isActive()).build();
    }

    public static AppointmentResponse toAppointment(Appointment a) {
        return AppointmentResponse.builder().id(a.getId()).patientId(a.getPatient().getId())
                .patientName(a.getPatient().getFullName()).doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName()).appointmentTime(a.getAppointmentTime()).status(a.getStatus())
                .symptomDescription(a.getSymptomDescription()).build();
    }

    public static MedicalRecordResponse toRecord(MedicalRecord r) {
        return MedicalRecordResponse.builder().id(r.getId()).appointmentId(r.getAppointment().getId())
                .patientId(r.getPatient().getId()).patientName(r.getPatient().getFullName())
                .doctorId(r.getDoctor().getId()).doctorName(r.getDoctor().getFullName()).diagnosis(r.getDiagnosis())
                .fileUrl(r.getFileUrl()).createdAt(r.getCreatedAt()).build();
    }
}
