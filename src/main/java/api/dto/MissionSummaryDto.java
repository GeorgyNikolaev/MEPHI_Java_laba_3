package api.dto;

import enums.MissionOutcome;

import java.time.Instant;
import java.time.LocalDate;

public record MissionSummaryDto(
        Long id,
        String missionCode,
        LocalDate date,
        String location,
        MissionOutcome outcome,
        Instant createdAt,
        String sourceFilename
) {
}
