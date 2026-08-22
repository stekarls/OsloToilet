package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.ContributionPoints;
import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.toilet.ToiletRequestDto;
import com.app.oslotoilet.toilet.ToiletService;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LocationRequestService {

    private final LocationRequestRepository locationRequestRepository;
    private final UserRepository userRepository;
    private final ToiletService toiletService;

    public LocationRequestService(LocationRequestRepository locationRequestRepository, UserRepository userRepository, ToiletService toiletService){
        this.locationRequestRepository = locationRequestRepository;
        this.userRepository = userRepository;
        this.toiletService = toiletService;
    }

    @Transactional
    public LocationRequestResponseDto createNewLocationRequest(LocationRequestDto locationRequest){
        User user = userRepository.findById(locationRequest.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + locationRequest.getUserId()));

        LocationRequest request = mapToEntity(locationRequest, user);
        request = locationRequestRepository.save(request);

        return mapToResponseDto(request);
    }

    public boolean deleteLocationRequestById(UUID id){
        if (locationRequestRepository.existsById(id)){
            locationRequestRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public List<LocationRequestResponseDto> getAllRequests(){
        return locationRequestRepository.findAllWithUser().stream().map(this::mapToResponseDto).toList();
    }

    public List<LocationRequestResponseDto> findByRequestStatus(RequestStatus requestStatus){
        return locationRequestRepository.findByRequestStatus(requestStatus).stream().map(this::mapToResponseDto).toList();
    }

    public List<LocationRequestResponseDto> findByuserIdAndRequestStatus(UUID userId, RequestStatus requestStatus){
        return locationRequestRepository.findByuserIdAndRequestStatus(userId, requestStatus).stream().map(this::mapToResponseDto).toList();
    }

    public List<LocationRequestResponseDto> findByUserIdOrderByCreatedAtDesc(UUID userId){
        return locationRequestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToResponseDto).toList();
    }

    @Transactional
    public LocationRequestResponseDto changeRequestStatus(UUID locationRequestId, RequestStatus newStatus, String adminComment){

        LocationRequest request = locationRequestRepository.findById(locationRequestId).orElseThrow(() ->
                new EntityNotFoundException("Location Request not found with ID: " + locationRequestId));

        if (request.getRequestStatus() == RequestStatus.APPROVED){
            throw new IllegalStateException("Cannot modify a location request that has already been approved");
        }

        if(adminComment != null){
            request.setAdminComment(adminComment);
        }

        request.setRequestStatus(newStatus);

        if (newStatus == RequestStatus.APPROVED){
            User user = request.getUser();
            user.setContributionPoints(user.getContributionPoints() + ContributionPoints.APPROVED.getValue());
        }

        ToiletRequestDto newToilet = ToiletRequestDto.builder()
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .hasFee(request.isHasFee())
                .fee(request.getFee())
                .description(request.getDescription())
                .hasConditions(false)
                .isSeasonal(false)
                .isClosed(false)
                .build();

        toiletService.createToilet(newToilet);

        return mapToResponseDto(request);
    }



    private LocationRequest mapToEntity(LocationRequestDto locationRequestDto, User user){
        BigDecimal fee = locationRequestDto.isHasFee() ? locationRequestDto.getFee() : BigDecimal.valueOf(0);
        return LocationRequest.builder()
                .user(user)
                .name(locationRequestDto.getName())
                .latitude(locationRequestDto.getLatitude())
                .longitude(locationRequestDto.getLongitude())
                .description(locationRequestDto.getDescription())
                .hasFee(locationRequestDto.isHasFee())
                .fee(fee)
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
