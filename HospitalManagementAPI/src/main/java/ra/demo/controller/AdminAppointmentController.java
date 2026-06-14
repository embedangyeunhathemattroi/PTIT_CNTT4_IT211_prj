package ra.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.demo.model.dto.request.AppointmentStatusRequest;
import ra.demo.model.dto.response.ApiDataResponse;
import ra.demo.model.dto.response.AppointmentResponse;
import ra.demo.service.AppointmentService;

@RestController
@RequestMapping("/api/v1/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService service;

    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<AppointmentResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get appointments successfully",
                        service.findAll(
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by("appointmentTime")
                                                .descending()
                                )
                        ),
                        200
                )
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiDataResponse<AppointmentResponse>> status(
            @PathVariable Long id,
            @Valid
            @RequestBody AppointmentStatusRequest r) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Update appointment status successfully",
                        service.updateStatus(id, r),
                        200
                )
        );
    }
}