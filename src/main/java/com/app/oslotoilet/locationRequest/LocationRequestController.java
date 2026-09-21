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
@RequestMapping("/api/v1/location-requests")
public class LocationRequestController {

    private final LocationRequestService locationRequestService;

    public LocationRequestController(LocationRequestService locationRequestService){
        this.locationRequestService = locationRequestService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<LocationRequestResponseDto>> getRequests(@RequestParam(required = false) RequestStatus status){
        if (status != null){
            return ResponseEntity.ok(locationRequestService.getByRequestStatus(status));
        }
        return ResponseEntity.ok(locationRequestService.getAllRequests());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<LocationRequestResponseDto> getByLocationRequestId(@PathVariable UUID requestId, @AuthenticationPrincipal SecurityUser currentUser){
        return ResponseEntity.ok(locationRequestService.getByLocationRequestId(requestId, currentUser));
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LocationRequestResponseDto>> getRequestsByUser(@PathVariable UUID userId, @RequestParam(required = false) RequestStatus status){
        if (status != null){
            return ResponseEntity.ok(locationRequestService.getByUserIdAndRequestStatus(userId, status));
        }
        return ResponseEntity.ok(locationRequestService.getByUserIdOrderByCreatedAtDesc(userId));
    }

    @PostMapping
    public ResponseEntity<LocationRequestResponseDto> createNewLocationRequest(@Valid @RequestBody LocationRequestDto locationRequest, @AuthenticationPrincipal SecurityUser currentUser){
        LocationRequestResponseDto response = locationRequestService.createNewLocationRequest(locationRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{locationRequestId}")
    public ResponseEntity<Void> deleteLocationRequestbyId(@PathVariable UUID locationRequestId, @AuthenticationPrincipal SecurityUser currentUser){
            locationRequestService.deleteLocationRequestById(locationRequestId, currentUser);
            return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<LocationRequestResponseDto> updateRequestStatus(@PathVariable UUID id, @RequestBody @Valid LocationRequestUpdateDto dto){
        LocationRequestResponseDto request = locationRequestService.updateRequestStatus(id, dto);
        return ResponseEntity.ok(request);
    }

}
