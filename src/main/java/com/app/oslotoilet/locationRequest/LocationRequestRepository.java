package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRequestRepository extends JpaRepository<LocationRequest, UUID> {

    //TODO: check other queries for n+1 problem
    //Solving n+1
    @Query("SELECT lr FROM LocationRequest lr JOIN FETCH lr.user")
    List<LocationRequest> findAllWithUser();

    List<LocationRequest> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<LocationRequest> findByRequestStatus(RequestStatus requestStatus);

    List<LocationRequest> findByuserIdAndRequestStatus(UUID userId, RequestStatus requestStatus);

}
