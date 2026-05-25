package com.app.oslotoilet.errorReport;

import com.app.oslotoilet.contribution.RequestStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @GetMapping
    public ResponseEntity<List<ErrorReportDto>> getErrorReports(@RequestParam(required = false)RequestStatus status){
        if (status != null){
            return new ResponseEntity<>(errorReportService.findByRequestStatus(status), HttpStatus.OK);
        }
        return new ResponseEntity<>(errorReportService.getErrorReports(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ErrorReportDto> createErrorReport(ErrorReportDto errorReportDto){
        ErrorReportDto report = errorReportService.createErrorReport(errorReportDto);
        return new ResponseEntity<>(report, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorReport(@PathVariable UUID id){
        if (errorReportService.deleteErrorReport(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ErrorReportDto> changeStatus(@PathVariable UUID id, @RequestParam RequestStatus status, @RequestParam(required = false) String adminComment){
        ErrorReportDto report = errorReportService.changeStatus(id, status, adminComment);
        return ResponseEntity.ok(report);
    }


}
