package ra.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.demo.model.dto.request.AppointmentRequest;
import ra.demo.model.dto.response.ApiDataResponse;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.service.AppointmentService;
import ra.demo.service.MedicalRecordService;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientAppointmentController {
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @PostMapping("/appointments")
    public ResponseEntity<ApiDataResponse<AppointmentResponse>> create(
            @Valid @RequestBody AppointmentRequest r, Authentication a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDataResponse.success("Create appointment successfully",
                appointmentService.create(r, a.getName()), 201));
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiDataResponse<Page<AppointmentResponse>>> history(Authentication a,
                                                                              @RequestParam(defaultValue = "0")
                                                                              int page,
                                                                              @RequestParam(defaultValue = "5")
                                                                              int size) {
        return ResponseEntity.ok(ApiDataResponse.success("Get appointment history successfully",
                appointmentService.patientHistory(a.getName(),
                        PageRequest.of(page, size, Sort.by("appointmentTime").descending())), 200));
    }

    @GetMapping("/records")
    public ResponseEntity<ApiDataResponse<Page<MedicalRecordResponse>>> records(Authentication a,
                                                                                @RequestParam(defaultValue = "0")
                                                                                int page,
                                                                                @RequestParam(defaultValue = "5")
                                                                                int size) {
        return ResponseEntity.ok(ApiDataResponse.success("Get medical record history successfully",
                medicalRecordService.patientRecords(a.getName(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending())), 200));
    }
}
