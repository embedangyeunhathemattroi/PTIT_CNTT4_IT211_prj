package ra.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.demo.model.entity.Appointment;
import ra.demo.model.entity.User;
import ra.demo.model.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorAndAppointmentTimeAndStatusIn(User doctor, LocalDateTime appointmentTime,
                                                        Collection<AppointmentStatus> statuses);

    Page<Appointment> findByPatient(User patient, Pageable pageable);

    Page<Appointment> findByDoctor(User doctor, Pageable pageable);
}
