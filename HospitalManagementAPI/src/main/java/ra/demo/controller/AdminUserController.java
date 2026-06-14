package ra.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.demo.model.dto.request.UserRequest;
import ra.demo.model.dto.response.ApiDataResponse;
import ra.demo.model.dto.response.UserResponse;
import ra.demo.service.UserService;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<UserResponse>>> list(
            @RequestParam(defaultValue = "")
            String keyword,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size,
            @RequestParam(defaultValue = "id")
            String sort) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get users successfully",
                        service.findAll(
                                keyword,
                                PageRequest.of(
                                        page,
                                        size,
                                        Sort.by(sort).descending()
                                )
                        ),
                        200
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<UserResponse>> detail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Get user successfully",
                        service.findById(id),
                        200
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<UserResponse>> create(
            @Valid
            @RequestBody UserRequest r) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiDataResponse.success(
                                "Create user successfully",
                                service.create(r),
                                201
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<UserResponse>> update(
            @PathVariable Long id,
            @Valid
            @RequestBody UserRequest r) {

        return ResponseEntity.ok(
                ApiDataResponse.success(
                        "Update user successfully",
                        service.update(id, r),
                        200
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}