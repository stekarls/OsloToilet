package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.errorReport.ErrorReport;
import com.app.oslotoilet.errorReport.ErrorReportRepository;
import com.app.oslotoilet.errorReport.ErrorReportService;
import com.app.oslotoilet.errorReport.ErrorReportUpdateDto;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ErrorReportServiceTests {

    @Mock
    private ErrorReportRepository errorReportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ToiletRepository toiletRepository;

    @InjectMocks
    private ErrorReportService errorReportService;

    private User reporter;
    private ErrorReport report;

    @BeforeEach
    void setUp() {
        reporter = User.builder().id(UUID.randomUUID()).role(Role.USER).contributionPoints(100L).build();
        report = ErrorReport.builder()
                .id(UUID.randomUUID())
                .user(reporter)
                .toilet(Toilet.builder().id(UUID.randomUUID()).build())
                .description("Does not take Vipps anymore")
                .status(RequestStatus.PENDING)
                .build();
        when(errorReportRepository.findById(report.getId())).thenReturn(Optional.of(report));
    }

    private ErrorReportUpdateDto status(RequestStatus status) {
        return ErrorReportUpdateDto.builder().status(status).build();
    }

    @Test
    void changeStatus_shouldGiveTheReporterPoints_whenReportIsFixed() {
        errorReportService.changeStatus(report.getId(), status(RequestStatus.FIXED));

        assertEquals(130L, reporter.getContributionPoints());
    }

    @Test
    void changeStatus_shouldOnlyGivePointsOnce_whenFixedReportIsMarkedFixedAgain() {
        errorReportService.changeStatus(report.getId(), status(RequestStatus.FIXED));
        errorReportService.changeStatus(report.getId(), status(RequestStatus.FIXED));

        assertEquals(130L, reporter.getContributionPoints());
    }

    //Only a fixed report proves the user found a real error, so every other status gives nothing
    @ParameterizedTest
    @EnumSource(value = RequestStatus.class, names = "FIXED", mode = EnumSource.Mode.EXCLUDE)
    void changeStatus_shouldNotGivePoints_forAnyOtherStatus(RequestStatus status) {
        errorReportService.changeStatus(report.getId(), status(status));

        assertEquals(100L, reporter.getContributionPoints());
    }

    @Test
    void changeStatus_shouldReject_andKeepPoints_whenFixedReportIsMovedToAnotherStatus() {
        errorReportService.changeStatus(report.getId(), status(RequestStatus.FIXED));

        assertThrows(IllegalStateException.class, () -> errorReportService.changeStatus(report.getId(), status(RequestStatus.REJECTED)));

        assertEquals(RequestStatus.FIXED, report.getStatus());
        assertEquals(130L, reporter.getContributionPoints());
    }

    @Test
    void changeStatus_shouldStillAllowAdminComment_onFixedReport() {
        errorReportService.changeStatus(report.getId(), status(RequestStatus.FIXED));

        errorReportService.changeStatus(report.getId(), ErrorReportUpdateDto.builder().adminComment("Updated payment options").build());

        assertEquals("Updated payment options", report.getAdminComment());
    }
}
