package com.app.oslotoilet.locationRequest;


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
@RequestMapping("/api/v1/contribution")
public class LocationRequestController {

    private final LocationRequestService locationRequestService;

    public LocationRequestController(LocationRequestService locationRequestService){
        this.locationRequestService = locationRequestService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<LocationRequestResponseDto> getRequests(@RequestParam(required = false) RequestStatus status){
        if (status != null){
            return locationRequestService.getByRequestStatus(status);
        }
        return locationRequestService.getAllRequests();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public List<LocationRequestResponseDto> getRequestsByUser(@PathVariable UUID userId, @RequestParam(required = false) RequestStatus requestStatus){
        if (requestStatus != null){
            return locationRequestService.getByUserIdAndRequestStatus(userId, requestStatus);
        }
        return locationRequestService.getByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping("/create")
    public ResponseEntity<LocationRequestResponseDto> createNewLocationRequest(@Valid @RequestBody LocationRequestDto locationRequest){
        LocationRequestResponseDto response = locationRequestService.createNewLocationRequest(locationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @DeleteMapping("/{locationRequestId}")
    public ResponseEntity<Void> deleteLocationRequestbyId(@PathVariable UUID locationRequestId, @AuthenticationPrincipal SecurityUser currentUser){
            locationRequestService.deleteLocationRequestById(locationRequestId, currentUser);
            return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<LocationRequestResponseDto> approveRequestStatus(@PathVariable UUID id, @RequestParam RequestStatus requestStatus, @RequestParam(required = false) String adminComment){
        LocationRequestResponseDto request = locationRequestService.approveRequestStatus(id, requestStatus, adminComment);
        return ResponseEntity.ok(request);
    }

}
