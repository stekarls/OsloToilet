package com.app.oslotoilet.errorReport;


import com.app.oslotoilet.contribution.LocationRequest;
import com.app.oslotoilet.contribution.RequestStatus;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
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

    public List<ErrorReportDto> findByRequestStatus(RequestStatus status){
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

    public boolean deleteErrorReport(UUID id){
        if (errorReportRepository.existsById(id)){
            errorReportRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ErrorReportDto changeStatus(UUID id, RequestStatus status, String adminComment){
        ErrorReport errorReport = errorReportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error report not found with ID: " + id));

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
