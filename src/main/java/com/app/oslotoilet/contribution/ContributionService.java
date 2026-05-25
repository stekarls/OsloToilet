package com.app.oslotoilet.contribution;

import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final UserRepository userRepository;

    public ContributionService(ContributionRepository contributionRepository, UserRepository userRepository){
        this.contributionRepository = contributionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LocationRequestResponseDto createNewLocationRequest(LocationRequestDto locationRequest){
        User user = userRepository.findById(locationRequest.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + locationRequest.getUserId()));

        LocationRequest request = mapToEntity(locationRequest, user);
        request = contributionRepository .save(request);

        return mapToResponseDto(request);
    }

    public boolean deleteLocationRequestById(UUID id){
        if (contributionRepository.existsById(id)){
            contributionRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public List<LocationRequestResponseDto> getAllRequests(){
        return contributionRepository.findAllWithUser().stream().map(this::mapToResponseDto).toList();
    }

    public List<LocationRequestResponseDto> findByRequestStatus(RequestStatus requestStatus){
        return contributionRepository.findByRequestStatus(requestStatus).stream().map(this::mapToResponseDto).toList();
    }

    public List<LocationRequestResponseDto> findByuserIdAndRequestStatus(UUID userId, RequestStatus requestStatus){
        return contributionRepository.findByuserIdAndRequestStatus(userId, requestStatus).stream().map(this::mapToResponseDto).toList();
    }

    public List<LocationRequestResponseDto> findByUserIdOrderByCreatedAtDesc(UUID userId){
        return contributionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToResponseDto).toList();
    }

    @Transactional
    public LocationRequestResponseDto changeRequestStatus(UUID id, RequestStatus newStatus, String adminComment){

        LocationRequest request = contributionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Location Request not found with ID: " + id));

        if (request.getRequestStatus() == RequestStatus.APPROVED){
            throw new IllegalStateException("Cannot modify a location request that has already been approved.");
        }

        if(adminComment != null){
            request.setAdminComment(adminComment);
        }


        request.setRequestStatus(newStatus);

        if (newStatus == RequestStatus.APPROVED){
            User user = request.getUser();
            user.setContributionPoints(user.getContributionPoints() + 100);
        }
        return mapToResponseDto(request);
    }



    private LocationRequest mapToEntity(LocationRequestDto locationRequestDto, User user){
        return LocationRequest.builder().user(user)
                .name(locationRequestDto.getName())
                .latitude(locationRequestDto.getLatitude())
                .longitude(locationRequestDto.getLongitude())
                .description(locationRequestDto.getDescription())
                .hasFee(locationRequestDto.isHasFee())
                .fee(locationRequestDto.getFee())
                .adminComment("")
                .requestStatus(RequestStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

    }

    private LocationRequestResponseDto mapToResponseDto(LocationRequest entity){
        return LocationRequestResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .description(entity.getDescription())
                .requestStatus(entity.getRequestStatus())
                .hasFee(entity.isHasFee())
                .fee(entity.getFee())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }





}
