package parser;

import domain.Mission;
import enums.MissionOutcome;
import enums.ThreatLevel;
import exception.MissionParsingException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MissionConverterTest {

    @Test
    void convertToMission_validMap_producesMission() throws Exception {
        Map<String, Object> data = validMissionMap();

        Mission mission = MissionConverter.convertToMission(data);

        assertEquals("M-UNIT-01", mission.getMissionId());
        assertEquals(MissionOutcome.SUCCESS, mission.getOutcome());
        assertEquals("Curse X", mission.getCurse().getName());
        assertEquals(ThreatLevel.HIGH, mission.getCurse().getThreatLevel());
        assertEquals(1, mission.getSorcerers().size());
    }

    @Test
    void convertToMission_missingCurse_throws() {
        Map<String, Object> data = validMissionMap();
        data.remove("curse");

        MissionParsingException ex = assertThrows(
                MissionParsingException.class,
                () -> MissionConverter.convertToMission(data));
        assertTrue(ex.getMessage().contains("curse"));
    }

    @Test
    void convertToMission_missingMissionId_throws() {
        Map<String, Object> data = validMissionMap();
        data.remove("missionId");

        assertThrows(MissionParsingException.class, () -> MissionConverter.convertToMission(data));
    }

    @Test
    void convertToMission_invalidOutcome_throws() {
        Map<String, Object> data = validMissionMap();
        data.put("outcome", "WRONG_VALUE");

        assertThrows(MissionParsingException.class, () -> MissionConverter.convertToMission(data));
    }

    private static Map<String, Object> validMissionMap() {
        Map<String, Object> curse = new HashMap<>();
        curse.put("name", "Curse X");
        curse.put("threatLevel", "HIGH");

        Map<String, Object> sorcerer = new HashMap<>();
        sorcerer.put("name", "Bob");
        sorcerer.put("rank", "GRADE_1");

        Map<String, Object> data = new HashMap<>();
        data.put("missionId", "M-UNIT-01");
        data.put("date", "2024-06-01");
        data.put("location", "Kyoto");
        data.put("outcome", "SUCCESS");
        data.put("curse", curse);
        data.put("sorcerers", List.of(sorcerer));
        return data;
    }
}
