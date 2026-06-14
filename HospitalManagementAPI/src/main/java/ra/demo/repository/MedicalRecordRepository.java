package ra.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.MedicalRecord;
import ra.demo.model.entity.User;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Page<MedicalRecord> findByPatient(
            User patient,
            Pageable pageable
    );

    Page<MedicalRecord> findByDoctor(
            User doctor,
            Pageable pageable
    );

    boolean existsByAppointment(
            Appointment appointment
    );
}