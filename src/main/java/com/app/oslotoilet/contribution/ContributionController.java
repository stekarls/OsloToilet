package com.app.oslotoilet.contribution;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/v1/contribution")
public class ContributionController {

    private final ContributionService contributionService;
    private final JsonMapper.Builder builder;

    public ContributionController(ContributionService contributionService, JsonMapper.Builder builder){
        this.contributionService = contributionService;
        this.builder = builder;
    }


    public ResponseEntity<Void> createNewLocationRequest(LocationRequest locationRequest){
        //TODO: check https status codes, change to correct
        if (contributionService.createNewLocationRequest(locationRequest)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<LocationRequest> deleteLocationRequest(LocationRequest locationRequest){
        if (contributionService.deleteLocationRequest(locationRequest)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
