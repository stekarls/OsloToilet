package com.app.oslotoilet.errorReport;


import com.app.oslotoilet.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ErrorReportRepository extends JpaRepository<ErrorReport, UUID> {

    //TODO: check other queries for n+1 problem

    List<ErrorReport> findByStatus(RequestStatus requestStatus);
}
