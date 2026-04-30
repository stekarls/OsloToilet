package com.app.oslotoilet.contribution;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public boolean deleteLocationRequestById(UUID id){
        if (contributionRepository.existsById(id)){
            contributionRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public List<LocationRequest> getAllRequests(){
        return contributionRepository.findAll();
    }

    public List<LocationRequest> findByRequestStatus(RequestStatus requestStatus){
        return contributionRepository.findByRequestStatus(requestStatus);
    }
    public List<LocationRequest> findByuserIdAndRequestStatus(UUID userId, RequestStatus requestStatus){
        return contributionRepository.findByuserIdAndRequestStatus(userId, requestStatus);
    }

    public List<LocationRequest> findByUserIdOrderByCreatedAtDesc(UUID userId){
        return contributionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }





}
