package com.app.oslotoilet.locationRequest;


import com.app.oslotoilet.enums.RequestStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @GetMapping
    public List<LocationRequestResponseDto> getRequests(@RequestParam(required = false) RequestStatus status){
        if (status != null){
            return locationRequestService.findByRequestStatus(status);
        }
        return locationRequestService.getAllRequests();
    }

    @PostMapping("/create")
    public ResponseEntity<LocationRequestResponseDto> createNewLocationRequest(@Valid @RequestBody LocationRequestDto locationRequest){
        LocationRequestResponseDto response = locationRequestService.createNewLocationRequest(locationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationRequestbyId(@PathVariable UUID id){
        if (locationRequestService.deleteLocationRequestById(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}")
    public List<LocationRequestResponseDto> getRequestsByUser(@PathVariable UUID userId, @RequestParam(required = false) RequestStatus requestStatus){
        if (requestStatus != null){
            return locationRequestService.findByuserIdAndRequestStatus(userId, requestStatus);
        }
        return locationRequestService.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PutMapping("{id}")
    public ResponseEntity<LocationRequestResponseDto> changeRequestStatus(@PathVariable UUID id, @RequestParam RequestStatus requestStatus, @RequestParam(required = false) String adminComment){
        LocationRequestResponseDto request = locationRequestService.changeRequestStatus(id, requestStatus, adminComment);
        return ResponseEntity.ok(request);
    }

}
