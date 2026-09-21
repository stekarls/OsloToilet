package com.app.oslotoilet.errorReport;

import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/error-reports")
public class ErrorReportController {

    private final ErrorReportService errorReportService;

    public ErrorReportController(ErrorReportService errorReportService){
        this.errorReportService = errorReportService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ErrorReportResponseDto>> getErrorReports(@RequestParam(required = false)RequestStatus status){
        if (status != null){
            return ResponseEntity.ok(errorReportService.getByRequestStatus(status));
        }
        return ResponseEntity.ok(errorReportService.getErrorReports());
    }

    @PostMapping
    public ResponseEntity<ErrorReportResponseDto> createErrorReport(@RequestBody @Valid ErrorReportRequestDto errorReportRequestDto, @AuthenticationPrincipal SecurityUser currentUser){
        ErrorReportResponseDto report = errorReportService.createErrorReport(errorReportRequestDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorReport(@PathVariable UUID id, @AuthenticationPrincipal SecurityUser currentUser){
        errorReportService.deleteErrorReport(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ErrorReportResponseDto> changeStatus(@PathVariable UUID id, @RequestBody @Valid ErrorReportUpdateDto dto){
        ErrorReportResponseDto report = errorReportService.changeStatus(id, dto);
        return ResponseEntity.ok(report);
    }


}
