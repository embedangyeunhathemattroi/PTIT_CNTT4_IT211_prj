package ra.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.dto.request.MedicalRecordRequest;
import ra.demo.model.dto.response.ApiDataResponse;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.service.AppointmentService;
import ra.demo.service.MedicalRecordService;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @GetMapping("/appointments")
    public ResponseEntity<ApiDataResponse<Page<AppointmentResponse>>> list(
            Authentication a,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get doctor appointments successfully",
                        appointmentService.doctorAppointments(
                                a.getName(),
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by("appointmentTime").descending()
                                )
                        ),
                        200
                )
        );
    }

    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<ApiDataResponse<AppointmentResponse>> status(
            @PathVariable Long id,
            @Valid
            @RequestBody AppointmentStatusRequest r,
            Authentication a) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Update appointment status successfully",
                        appointmentService.updateStatusByDoctor(
                                id,
                                r,
                                a.getName()
                        ),
                        200
                )
        );
    }

    @PostMapping(
            value = "/records/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiDataResponse<MedicalRecordResponse>> upload(
            @Valid
            @ModelAttribute MedicalRecordRequest r,
            @RequestPart("file")
            MultipartFile file,
            Authentication a) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Upload medical record successfully",
                        medicalRecordService.upload(
                                r,
                                file,
                                a.getName()
                        ),
                        200
                )
        );
    }

    @GetMapping("/records")
    public ResponseEntity<ApiDataResponse<Page<MedicalRecordResponse>>> records(
            Authentication a,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get doctor records successfully",
                        medicalRecordService.doctorRecords(
                                a.getName(),
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by("createdAt").descending()
                                )
                        ),
                        200
                )
        );
    }
}