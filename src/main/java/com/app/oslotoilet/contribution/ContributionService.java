package com.app.oslotoilet.contribution;

import org.springframework.stereotype.Service;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;

    public ContributionService(ContributionRepository contributionRepository){
        this.contributionRepository = contributionRepository;
    }


    public boolean createNewLocationRequest(LocationRequest locationRequest){
        //TODO: Add business logic
        contributionRepository.save(locationRequest);
        return true;
    }

    public boolean deleteLocationRequest(LocationRequest locationRequest){
        contributionRepository.delete(locationRequest);
        return true;
    }




}
