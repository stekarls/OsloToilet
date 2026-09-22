package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.ContributionPoints;
import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.enums.PaymentCode;
import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.feature.Feature;
import com.app.oslotoilet.feature.FeatureRepository;
import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.paymentOption.PaymentOptionRepository;
import com.app.oslotoilet.security.SecurityUser;
import com.app.oslotoilet.toilet.ToiletRequestDto;
import com.app.oslotoilet.toilet.ToiletResponseDto;
import com.app.oslotoilet.toilet.ToiletService;
import com.app.oslotoilet.toiletFeature.ToiletFeatureService;
import com.app.oslotoilet.toiletPaymentOption.ToiletPaymentOptionService;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LocationRequestService {

    private final LocationRequestRepository locationRequestRepository;
    private final UserRepository userRepository;
    private final ToiletService toiletService;
    private final FeatureRepository featureRepository;
    private final PaymentOptionRepository paymentOptionRepository;
    private final ToiletFeatureService toiletFeatureService;
    private final ToiletPaymentOptionService toiletPaymentOptionService;

    public LocationRequestService(LocationRequestRepository locationRequestRepository, UserRepository userRepository, ToiletService toiletService,
                                  FeatureRepository featureRepository, PaymentOptionRepository paymentOptionRepository,
                                  ToiletFeatureService toiletFeatureService, ToiletPaymentOptionService toiletPaymentOptionService){
        this.locationRequestRepository = locationRequestRepository;
        this.userRepository = userRepository;
        this.toiletService = toiletService;
        this.featureRepository = featureRepository;
        this.paymentOptionRepository = paymentOptionRepository;
        this.toiletFeatureService = toiletFeatureService;
        this.toiletPaymentOptionService = toiletPaymentOptionService;
    }

    public List<LocationRequestResponseDto> getRequests(UUID userId, RequestStatus status){
        List<LocationRequest> requests;

        if (userId != null && status != null) {
            requests = locationRequestRepository.findByuserIdAndRequestStatus(userId, status);
        } else if (userId != null) {
            requests = locationRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (status != null) {
            requests = locationRequestRepository.findByRequestStatus(status);
        } else {
            requests = locationRequestRepository.findAllWithUser();
        }

        return requests.stream().map(this::mapToResponseDto).toList();
    }

    public LocationRequestResponseDto getByLocationRequestId(UUID requestId, SecurityUser currentUser){
        LocationRequest locationRequest = locationRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Location Request with id " + requestId + " not found"));

        boolean isAdmin = currentUser.getUser().getRole() == Role.ADMIN;

        if (!isAdmin && !locationRequest.getUser().getId().equals(currentUser.getUser().getId())) {
            throw new AccessDeniedException("You are not authorized to view this location request");
        }

        return mapToResponseDto(locationRequest);
    }

    @Transactional
    public LocationRequestResponseDto createNewLocationRequest(LocationRequestDto locationRequest, SecurityUser currentUser){
        validateFee(locationRequest.getFee(), locationRequest.getHasFee());

        UUID userId = currentUser.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        LocationRequest request = mapToEntity(locationRequest, user);
        request.setFeatures(findFeatures(locationRequest.getFeatureCodes()));
        request.setPaymentOptions(findPaymentOptions(locationRequest.getPaymentCodes()));
        request = locationRequestRepository.save(request);

        return mapToResponseDto(request);
    }

    @Transactional
    public void deleteLocationRequestById(UUID locationRequestId, SecurityUser currentUser){

        boolean isAdmin = currentUser.getUser().getRole() == Role.ADMIN;

        UUID userId = currentUser.getUser().getId();

        LocationRequest locationRequest = locationRequestRepository.findById(locationRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Location Request not found with ID: " + locationRequestId));

        if (!isAdmin && !locationRequest.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this location request");
        }

        locationRequestRepository.deleteById(locationRequestId);
    }

    @Transactional
    public LocationRequestResponseDto updateRequestStatus(UUID locationRequestId, LocationRequestUpdateDto dto){

        LocationRequest request = locationRequestRepository.findById(locationRequestId).orElseThrow(() ->
                new EntityNotFoundException("Location Request not found with ID: " + locationRequestId));

        if (request.getRequestStatus() == RequestStatus.APPROVED){
            throw new IllegalStateException("Cannot modify a location request that has already been approved");
        }

        if(dto.getAdminComment() != null){
            request.setAdminComment(dto.getAdminComment());
        }

        RequestStatus newStatus = dto.getRequestStatus();

        if (newStatus != null){
            request.setRequestStatus(newStatus);
        }

        if (newStatus == RequestStatus.APPROVED){
            User user = request.getUser();
            user.setContributionPoints(user.getContributionPoints() + ContributionPoints.APPROVED.getValue());
            ToiletRequestDto newToilet = ToiletRequestDto.builder()
                    .name(request.getName())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .hasFee(request.isHasFee())
                    .fee(request.getFee())
                    .description(request.getDescription())
                    .isSeasonal(false)
                    .isClosed(false)
                    .build();

            ToiletResponseDto toilet = toiletService.createToilet(newToilet);
            toiletFeatureService.addUserContributedFeatures(toilet.getId(), request.getFeatures());
            toiletPaymentOptionService.addUserContributedPaymentOptions(toilet.getId(), request.getPaymentOptions());
        }
        return mapToResponseDto(request);
    }


    private void validateFee(BigDecimal fee, boolean hasFee){
        if (hasFee && (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Fee must be a value greater than 0 when hasFee is true");
        }
    }

    private Set<Feature> findFeatures(Set<FeatureCode> codes){
        if (codes == null || codes.isEmpty()) {
            return new HashSet<>();
        }
        List<Feature> features = featureRepository.findByFeatureCodeIn(codes);
        if (features.size() != codes.size()) {
            throw new EntityNotFoundException("One or more features not found");
        }
        return new HashSet<>(features);
    }

    private Set<PaymentOption> findPaymentOptions(Set<PaymentCode> codes){
        if (codes == null || codes.isEmpty()) {
            return new HashSet<>();
        }
        List<PaymentOption> paymentOptions = paymentOptionRepository.findByCodeIn(codes);
        if (paymentOptions.size() != codes.size()) {
            throw new EntityNotFoundException("One or more payment options not found");
        }
        return new HashSet<>(paymentOptions);
    }

    private LocationRequest mapToEntity(LocationRequestDto locationRequestDto, User user){
        BigDecimal fee = locationRequestDto.getHasFee() ? locationRequestDto.getFee() : null;
        return LocationRequest.builder()
                .user(user)
                .name(locationRequestDto.getName())
                .latitude(locationRequestDto.getLatitude())
                .longitude(locationRequestDto.getLongitude())
                .description(locationRequestDto.getDescription())
                .hasFee(locationRequestDto.getHasFee())
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
                .featureCodes(entity.getFeatures().stream().map(Feature::getFeatureCode).collect(Collectors.toSet()))
                .paymentCodes(entity.getPaymentOptions().stream().map(PaymentOption::getCode).collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }





}
