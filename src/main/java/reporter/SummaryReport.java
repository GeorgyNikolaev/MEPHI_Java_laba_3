package reporter;


import domain.Mission;
import enums.ReportType;

public class SummaryReport implements MissionReporter {

    @Override
    public Report generate(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== КРАТКИЙ ОТЧЕТ О МИССИИ ===\n\n");
        sb.append("ID миссии: ").append(mission.getMissionId()).append("\n");
        sb.append("Дата: ").append(mission.getDate()).append("\n");
        sb.append("Локация: ").append(mission.getLocation()).append("\n");
        sb.append("Результат: ").append(mission.getOutcome()).append("\n");
        sb.append("Проклятие: ").append(mission.getCurse().getName())
                .append(" (").append(mission.getCurse().getThreatLevel()).append(")\n");

        if (mission.getSorcerers() != null && !mission.getSorcerers().isEmpty()) {
            sb.append("Участников: ").append(mission.getSorcerers().size()).append("\n");
        }

        return new Report(sb.toString(), ReportType.SUMMARY);
    }

    @Override
    public ReportType getType() {
        return ReportType.SUMMARY;
    }
}
