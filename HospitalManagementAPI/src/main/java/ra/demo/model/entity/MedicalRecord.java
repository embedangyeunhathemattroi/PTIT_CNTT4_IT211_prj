package ra.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    private Appointment appointment;
    @ManyToOne(optional = false)
    private User doctor;
    @ManyToOne(optional = false)
    private User patient;
    @Column(length = 2000)
    private String diagnosis;
    private String fileUrl;
    private LocalDateTime createdAt;
}
