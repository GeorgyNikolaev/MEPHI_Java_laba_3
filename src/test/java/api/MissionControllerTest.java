package api;

import api.dto.MissionDetailDto;
import api.dto.MissionSummaryDto;
import api.dto.ReportResponseDto;
import api.error.ApiExceptionHandler;
import enums.MissionOutcome;
import enums.ReportType;
import exception.MissionParsingException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import service.MissionApplicationService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MissionControllerTest {

    private MockMvc mockMvc;
    private MissionApplicationService missionApplicationService;

    @BeforeEach
    void setUp() {
        missionApplicationService = Mockito.mock(MissionApplicationService.class);
        MissionController controller = new MissionController(missionApplicationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void listMissions_returnsOk() throws Exception {
        when(missionApplicationService.listMissions()).thenReturn(List.of(
                new MissionSummaryDto(1L, "M-1", LocalDate.now(), "Loc", MissionOutcome.SUCCESS,
                        Instant.now(), "a.json")
        ));

        mockMvc.perform(get("/api/missions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].missionCode").value("M-1"));
    }

    @Test
    void uploadMission_returnsCreated() throws Exception {
        when(missionApplicationService.uploadMission(any()))
                .thenReturn(new MissionSummaryDto(2L, "M-2", LocalDate.now(), "Loc", MissionOutcome.SUCCESS,
                        Instant.now(), "b.json"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "mission.json", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes());

        mockMvc.perform(multipart("/api/missions").file(file).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.missionCode").value("M-2"));
    }

    @Test
    void uploadMission_invalidFile_returnsBadRequest() throws Exception {
        when(missionApplicationService.uploadMission(any()))
                .thenThrow(new MissionParsingException("Отсутствует блок curse"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.json", MediaType.APPLICATION_JSON_VALUE, "{".getBytes());

        mockMvc.perform(multipart("/api/missions").file(file).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Отсутствует блок curse"));
    }

    @Test
    void getMissionDetail_returnsOk() throws Exception {
        when(missionApplicationService.getMissionDetail(3L)).thenReturn(
                new MissionDetailDto(
                        3L, "M-3", LocalDate.of(2024, 1, 1), "City", MissionOutcome.SUCCESS,
                        100L, null, "m.json", Instant.now(),
                        null, List.of(), List.of(), List.of(), List.of()));

        mockMvc.perform(get("/api/missions/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.missionCode").value("M-3"));
    }

    @Test
    void getMissionDetail_notFound_returns404() throws Exception {
        when(missionApplicationService.getMissionDetail(404L))
                .thenThrow(new EntityNotFoundException("Mission not found: 404"));

        mockMvc.perform(get("/api/missions/404").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMission_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/missions/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(missionApplicationService).deleteMission(5L);
    }

    @Test
    void createReport_returnsCreatedWithContent() throws Exception {
        when(missionApplicationService.createReport(eq(7L), eq(ReportType.SUMMARY)))
                .thenReturn(new ReportResponseDto(
                        50L, 7L, ReportType.SUMMARY, "=== КРАТКИЙ ОТЧЕТ ===", Instant.now()));

        mockMvc.perform(post("/api/missions/7/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"reportType\":\"SUMMARY\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("SUMMARY"))
                .andExpect(jsonPath("$.content").value("=== КРАТКИЙ ОТЧЕТ ==="));
    }

    @Test
    void createReport_notFound_returns404() throws Exception {
        when(missionApplicationService.createReport(eq(9L), eq(ReportType.RISK)))
                .thenThrow(new EntityNotFoundException("Mission not found: 9"));

        mockMvc.perform(post("/api/missions/9/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"reportType\":\"RISK\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReports_returnsOk() throws Exception {
        when(missionApplicationService.listReports(2L)).thenReturn(List.of(
                new ReportResponseDto(1L, 2L, ReportType.DETAILED, "body", Instant.now())
        ));

        mockMvc.perform(get("/api/missions/2/reports").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("body"));
    }
}
