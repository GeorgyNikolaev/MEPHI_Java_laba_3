package reporter;

import domain.Mission;
import enums.ReportType;

public interface MissionReporter {
    Report generate(Mission mission);
    ReportType getType();
}