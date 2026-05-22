package api.dto;

import enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Тип генерируемого отчёта")
public record CreateReportRequest(
        @NotNull
        @Schema(description = "SUMMARY, DETAILED, RISK, STATISTICAL", example = "SUMMARY")
        ReportType reportType
) {
}
