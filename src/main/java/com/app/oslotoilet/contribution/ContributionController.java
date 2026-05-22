package com.app.oslotoilet.contribution;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contribution")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService){
        this.contributionService = contributionService;
    }


    @GetMapping
    public List<LocationRequestResponseDto> getRequests(@RequestParam(required = false) RequestStatus status){
        if (status != null){
            return contributionService.findByRequestStatus(status);
        }
        return contributionService.getAllRequests();
    }

    @PostMapping("/create")
    public ResponseEntity<LocationRequestResponseDto> createNewLocationRequest(@Valid @RequestBody LocationRequestDto locationRequest){
        LocationRequestResponseDto response = contributionService.createNewLocationRequest(locationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationRequestbyId(@PathVariable UUID id){
        if (contributionService.deleteLocationRequestById(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}")
    public List<LocationRequestResponseDto> getRequestsByUser(@PathVariable UUID userId, @RequestParam(required = false) RequestStatus status){
        if (status != null){
            return contributionService.findByuserIdAndRequestStatus(userId, status);
        }
        return contributionService.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
