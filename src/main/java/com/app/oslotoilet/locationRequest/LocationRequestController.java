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

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.user.id")
    @GetMapping
    public ResponseEntity<List<LocationRequestResponseDto>> getRequests(@RequestParam(required = false) UUID userId, @RequestParam(required = false) RequestStatus status){
        return ResponseEntity.ok(locationRequestService.getRequests(userId, status));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<LocationRequestResponseDto> getByLocationRequestId(@PathVariable UUID requestId, @AuthenticationPrincipal SecurityUser currentUser){
        return ResponseEntity.ok(locationRequestService.getByLocationRequestId(requestId, currentUser));
    }

    @PostMapping
    public ResponseEntity<LocationRequestResponseDto> createNewLocationRequest(@Valid @RequestBody LocationRequestDto locationRequest, @AuthenticationPrincipal SecurityUser currentUser){
        LocationRequestResponseDto response = locationRequestService.createNewLocationRequest(locationRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteLocationRequestById(@PathVariable UUID requestId, @AuthenticationPrincipal SecurityUser currentUser){
            locationRequestService.deleteLocationRequestById(requestId, currentUser);
            return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{requestId}")
    public ResponseEntity<LocationRequestResponseDto> updateRequestStatus(@PathVariable UUID requestId, @RequestBody @Valid LocationRequestUpdateDto dto){
        LocationRequestResponseDto request = locationRequestService.updateRequestStatus(requestId, dto);
        return ResponseEntity.ok(request);
    }

}
