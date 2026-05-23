package reporter;

import domain.Mission;
import enums.ReportType;
import support.MissionTestFixtures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SummaryReportTest {

    private final SummaryReport summaryReport = new SummaryReport();

    @Test
    void generate_containsMissionIdentityAndCurse() {
        Mission mission = MissionTestFixtures.sampleMission();

        Report report = summaryReport.generate(mission);

        assertEquals(ReportType.SUMMARY, report.getType());
        assertNotNull(report.getContent());
        assertTrue(report.getContent().contains("M-FIXTURE-01"));
        assertTrue(report.getContent().contains("Fixture curse"));
        assertTrue(report.getContent().contains("Участников: 1"));
    }

    @Test
    void getType_returnsSummary() {
        assertEquals(ReportType.SUMMARY, summaryReport.getType());
    }
}
