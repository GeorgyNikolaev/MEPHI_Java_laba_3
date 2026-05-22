package api;

import api.dto.CreateReportRequest;
import api.dto.MissionDetailDto;
import api.dto.MissionSummaryDto;
import api.dto.ReportResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import exception.MissionParsingException;
import service.MissionApplicationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "Missions", description = "Загрузка, просмотр и удаление миссий")
public class MissionController {

    private final MissionApplicationService missionService;

    public MissionController(MissionApplicationService missionService) {
        this.missionService = missionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить миссию из файла (JSON, XML, YAML, TXT и др.)")
    public ResponseEntity<MissionSummaryDto> upload(@RequestPart("file") MultipartFile file)
            throws MissionParsingException, IOException {
        MissionSummaryDto created = missionService.uploadMission(file);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    @Operation(summary = "Список загруженных миссий")
    public List<MissionSummaryDto> list() {
        return missionService.listMissions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детали миссии")
    public MissionDetailDto get(@PathVariable Long id) {
        return missionService.getMissionDetail(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить миссию и связанные отчёты")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reports")
    @Operation(summary = "Создать отчёт по миссии")
    public ResponseEntity<ReportResponseDto> createReport(
            @PathVariable Long id,
            @Valid @RequestBody CreateReportRequest request) {
        ReportResponseDto dto = missionService.createReport(id, request.reportType());
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{id}/reports")
    @Operation(summary = "Список сохранённых отчётов по миссии")
    public List<ReportResponseDto> listReports(@PathVariable Long id) {
        return missionService.listReports(id);
    }
}
