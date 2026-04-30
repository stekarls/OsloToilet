package com.app.oslotoilet.contribution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContributionRepository extends JpaRepository<LocationRequest, UUID> {

    List<LocationRequest> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<LocationRequest> findByRequestStatus(RequestStatus requestStatus);

    List<LocationRequest> findByuserIdAndRequestStatus(UUID userId, RequestStatus requestStatus);

}
