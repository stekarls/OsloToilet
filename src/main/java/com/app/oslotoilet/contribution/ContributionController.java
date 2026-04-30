package com.app.oslotoilet.contribution;


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
    public List<LocationRequest> getRequests(@RequestParam(required = false) RequestStatus status){
        if (status != null){
            return contributionService.findByRequestStatus(status);
        }
        return contributionService.getAllRequests();
    }

    @PostMapping("/new")
    public ResponseEntity<Void> createNewLocationRequest(@RequestBody LocationRequest locationRequest){
        //TODO: check https status codes, change to correct
        if (contributionService.createNewLocationRequest(locationRequest)){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationRequestbyId(@PathVariable UUID id){
        if (contributionService.deleteLocationRequestById(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}")
    public List<LocationRequest> getRequestsbyUser(@PathVariable UUID userId, @RequestParam(required = false) RequestStatus status){
        if (status != null){
            return contributionService.findByuserIdAndRequestStatus(userId, status);
        }
        return contributionService.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
