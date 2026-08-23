package com.app.oslotoilet.errorReport;


import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.security.SecurityUser;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ErrorReportService {

    private final ErrorReportRepository errorReportRepository;
    private final UserRepository userRepository;
    private final ToiletRepository toiletRepository;

    public ErrorReportService(ErrorReportRepository errorReportRepository, UserRepository userRepository, ToiletRepository toiletRepository){
        this.errorReportRepository = errorReportRepository;
        this.userRepository = userRepository;
        this.toiletRepository = toiletRepository;
    }

    public List<ErrorReportDto> getErrorReports(){
        return errorReportRepository.findAll().stream().map(this::mapToResponseDto).toList();
    }

    public List<ErrorReportDto> getByRequestStatus(RequestStatus status){
        return errorReportRepository.findByStatus(status).stream().map(this::mapToResponseDto).toList();
    }

    @Transactional
    public ErrorReportDto createErrorReport(ErrorReportDto errorReportDto){
        User user = userRepository.findById(errorReportDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + errorReportDto.getUserId()));
        Toilet toilet = toiletRepository.findById(errorReportDto.getToiletID()).orElseThrow(() -> new EntityNotFoundException("Toilet not found with ID: " + errorReportDto.getToiletID()));

        ErrorReport errorReport = mapToEntity(errorReportDto, user, toilet);
        errorReport =  errorReportRepository.save(errorReport);
        return mapToResponseDto(errorReport);
    }

    @Transactional
    public void deleteErrorReport(UUID id, SecurityUser currentUser){
        boolean isAdmin = currentUser.getUser().getRole() == Role.ADMIN;

        ErrorReport errorReport = errorReportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error report not found with ID: " + id));

        if (!isAdmin && !errorReport.getUser().getId().equals(currentUser.getUser().getId())){
            throw new AccessDeniedException("You do not have permission to delete this error report");
        }

        errorReportRepository.deleteById(id);

    }

    @Transactional
    public ErrorReportDto changeStatus(UUID reportId, RequestStatus status, String adminComment){
        ErrorReport errorReport = errorReportRepository.findById(reportId).orElseThrow(() -> new EntityNotFoundException("Error report not found with ID: " + reportId));

        if (adminComment != null){
            errorReport.setAdminComment(adminComment);
        }

        errorReport.setStatus(status);

        return mapToResponseDto(errorReport);

    }



    private ErrorReport mapToEntity(ErrorReportDto errorReportDto, User user, Toilet toilet){
        return ErrorReport.builder()
                .toilet(toilet)
                .user(user)
                .description(errorReportDto.getDescription())
                .created(OffsetDateTime.now())
                .adminComment("")
                .status(RequestStatus.PENDING)
                .build();
    }

    private ErrorReportDto mapToResponseDto(ErrorReport errorReport){
        return ErrorReportDto.builder()
                .toiletID(errorReport.getToilet().getId())
                .userId(errorReport.getUser().getId())
                .description(errorReport.getDescription())
                .build();

    }
}
