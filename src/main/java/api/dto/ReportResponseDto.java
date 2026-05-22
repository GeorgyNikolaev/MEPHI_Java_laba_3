package api.dto;

import enums.ReportType;

import java.time.Instant;

public record ReportResponseDto(
        Long id,
        Long missionId,
        ReportType reportType,
        String content,
        Instant createdAt
) {
}
