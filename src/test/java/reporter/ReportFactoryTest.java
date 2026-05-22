package reporter;

import enums.ReportType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportFactoryTest {

    @Test
    void createReporter_supportsAllTypes() {
        ReportFactory factory = new ReportFactory();
        assertTrue(factory.supportsReportType(ReportType.SUMMARY));
        assertTrue(factory.supportsReportType(ReportType.DETAILED));
        assertTrue(factory.supportsReportType(ReportType.RISK));
        assertTrue(factory.supportsReportType(ReportType.STATISTICAL));
    }

    @Test
    void createReporter_unknownType_throws() {
        ReportFactory factory = new ReportFactory();
        assertThrows(IllegalArgumentException.class, () -> factory.createReporter(null));
    }
}
