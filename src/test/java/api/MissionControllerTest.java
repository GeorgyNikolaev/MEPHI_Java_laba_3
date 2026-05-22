package api;

import api.dto.MissionSummaryDto;
import api.error.ApiExceptionHandler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import enums.MissionOutcome;

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
        Mockito.when(missionApplicationService.listMissions()).thenReturn(List.of(
                new MissionSummaryDto(1L, "M-1", LocalDate.now(), "Loc", MissionOutcome.SUCCESS,
                        Instant.now(), "a.json")
        ));

        mockMvc.perform(get("/api/missions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].missionCode").value("M-1"));
    }

    @Test
    void uploadMission_returnsCreated() throws Exception {
        Mockito.when(missionApplicationService.uploadMission(any()))
                .thenReturn(new MissionSummaryDto(2L, "M-2", LocalDate.now(), "Loc", MissionOutcome.SUCCESS,
                        Instant.now(), "b.json"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "mission.json", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes());

        mockMvc.perform(multipart("/api/missions").file(file).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.missionCode").value("M-2"));
    }

    @Test
    void deleteMission_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/missions/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
