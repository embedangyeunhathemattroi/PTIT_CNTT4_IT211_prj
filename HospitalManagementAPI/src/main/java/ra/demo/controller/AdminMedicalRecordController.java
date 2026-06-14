package ra.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.demo.model.dto.response.ApiDataResponse;
import ra.demo.model.dto.response.MedicalRecordResponse;
import ra.demo.service.MedicalRecordService;

@RestController
@RequestMapping("/api/v1/admin/records")
@RequiredArgsConstructor
public class AdminMedicalRecordController {

    private final MedicalRecordService service;

    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<MedicalRecordResponse>>> list(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get medical records successfully",
                        service.findAll(
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by("createdAt")
                                                .descending()
                                )
                        ),
                        200
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<MedicalRecordResponse>> detail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get medical record successfully",
                        service.findById(id),
                        200
                )
        );
    }
}