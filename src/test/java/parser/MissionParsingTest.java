package parser;

import domain.Mission;
import enums.MissionOutcome;
import enums.SorcererRank;
import enums.TechniqueType;
import exception.MissionParsingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MissionParsingTest {

    private final MissionParserFactory factory = new MissionParserFactory();

    @Test
    void parseValidJson_preservesMissionFieldsWithoutDistortion() throws Exception {
        Path file = resourcePath("missions/valid-mission.json");

        Mission mission = factory.getParser(file).parse(file);

        assertEquals("M-2024-017", mission.getMissionId());
        assertEquals("2024-10-12", mission.getDate().toString());
        assertEquals("Токио, район Сибуя", mission.getLocation());
        assertEquals(MissionOutcome.SUCCESS, mission.getOutcome());
        assertEquals(1_200_000L, mission.getDamageCost());
        assertNotNull(mission.getCurse());
        assertEquals("Проклятие подземного перехода", mission.getCurse().getName());
        assertEquals(2, mission.getSorcerers().size());
        assertEquals("Итадори Юдзи", mission.getSorcerers().get(0).getName());
        assertEquals(SorcererRank.GRADE_1, mission.getSorcerers().get(0).getRank());
        assertEquals(2, mission.getTechniques().size());
        assertEquals("Черная вспышка", mission.getTechniques().get(0).getTechnique().getName());
        assertEquals(TechniqueType.INNATE, mission.getTechniques().get(0).getTechnique().getType());
        assertEquals(500_000L, mission.getTechniques().get(0).getDamage());
        assertEquals("Итадори Юдзи", mission.getTechniques().get(0).getOwner().getName());
    }

    @Test
    void parseMalformedJson_rejectsDamagedFile() throws Exception {
        Path file = resourcePath("missions/invalid-malformed.json");

        assertThrows(MissionParsingException.class, () -> factory.getParser(file).parse(file));
    }

    @Test
    void parseMissingCurse_rejectsIncompleteMission() throws Exception {
        Path file = resourcePath("missions/invalid-missing-curse.json");

        MissionParsingException ex = assertThrows(
                MissionParsingException.class,
                () -> factory.getParser(file).parse(file));
        assertTrue(ex.getMessage().contains("curse"));
    }

    @Test
    void parseInvalidOutcome_rejectsIncorrectEnum() throws Exception {
        Path file = resourcePath("missions/invalid-bad-outcome.json");

        assertThrows(MissionParsingException.class, () -> factory.getParser(file).parse(file));
    }

    @Test
    void unsupportedExtension_rejectsUnknownFormat(@TempDir Path tempDir) {
        Path file = tempDir.resolve("mission.pdf");
        assertDoesNotThrow(() -> Files.writeString(file, "{}"));

        MissionParsingException ex = assertThrows(
                MissionParsingException.class,
                () -> factory.getParser(file));
        assertTrue(ex.getMessage().contains("Неподдерживаемое расширение"));
    }

    @Test
    void parseProjectSampleFromMissionsFolder_matchesExpectedId() throws Exception {
        Path file = Path.of("missions/A/Mission A.json");
        if (!Files.exists(file)) {
            file = resourcePath("missions/valid-mission.json");
        }

        Mission mission = factory.getParser(file).parse(file);
        assertEquals("M-2024-017", mission.getMissionId());
    }

    private static Path resourcePath(String classpathResource) throws Exception {
        URL url = MissionParsingTest.class.getClassLoader().getResource(classpathResource);
        assertNotNull(url, "Resource not found: " + classpathResource);
        return Path.of(url.toURI());
    }
}
