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
@RequestMapping("/api/v1/error")
public class ErrorReportController {

    private final ErrorReportService errorReportService;

    public ErrorReportController(ErrorReportService errorReportService){
        this.errorReportService = errorReportService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ErrorReportDto>> getErrorReports(@RequestParam(required = false)RequestStatus status){
        if (status != null){
            return new ResponseEntity<>(errorReportService.getByRequestStatus(status), HttpStatus.OK);
        }
        return new ResponseEntity<>(errorReportService.getErrorReports(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ErrorReportDto> createErrorReport(@RequestBody @Valid ErrorReportDto errorReportDto){
        ErrorReportDto report = errorReportService.createErrorReport(errorReportDto);
        return new ResponseEntity<>(report, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorReport(@PathVariable UUID id, @AuthenticationPrincipal SecurityUser currentUser){
        errorReportService.deleteErrorReport(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ErrorReportDto> changeStatus(@PathVariable UUID id, @RequestParam RequestStatus status, @RequestParam(required = false) String adminComment){
        ErrorReportDto report = errorReportService.changeStatus(id, status, adminComment);
        return ResponseEntity.ok(report);
    }


}
