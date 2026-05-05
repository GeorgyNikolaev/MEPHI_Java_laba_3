package reporter;

import enums.ReportType;

import java.util.HashMap;
import java.util.Map;

public class ReportFactory {
    private final Map<ReportType, MissionReporter> reporters;

    public ReportFactory() {
        this.reporters = new HashMap<>();
        registerDefaultReporters();
    }

    private void registerDefaultReporters() {
        registerReporter(ReportType.SUMMARY, new SummaryReport());
        registerReporter(ReportType.DETAILED, new DetailedReport());
        registerReporter(ReportType.RISK, new RiskReport());
        registerReporter(ReportType.STATISTICAL, new StatisticalReport());
        // Можно добавить RiskReport и StatisticalReport
    }

    public MissionReporter createReporter(ReportType type) {
        MissionReporter reporter = reporters.get(type);
        if (reporter == null) {
            throw new IllegalArgumentException("Неизвестный тип отчета: " + type);
        }
        return reporter;
    }

    public void registerReporter(ReportType type, MissionReporter reporter) {
        reporters.put(type, reporter);
    }

    public boolean supportsReportType(ReportType type) {
        return reporters.containsKey(type);
    }
}