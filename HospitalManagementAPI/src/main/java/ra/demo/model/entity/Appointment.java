package ra.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ra.demo.model.enums.AppointmentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private User patient;
    @ManyToOne(optional = false)
    private User doctor;
    @Column(nullable = false)
    private LocalDateTime appointmentTime;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    @Column(length = 1000)
    private String symptomDescription;
}
