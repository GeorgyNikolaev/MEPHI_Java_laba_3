package facade;

import domain.Mission;
import enums.ReportType;
import exception.MissionParsingException;
import parser.MissionParser;
import parser.MissionParserFactory;
import reporter.MissionReporter;
import reporter.Report;
import reporter.ReportFactory;

import java.nio.file.Path;

public class MissionAnalysisFacade {
    private MissionParserFactory parserFactory;
    private ReportFactory reportFactory;

    public MissionAnalysisFacade() {
        this.parserFactory = new MissionParserFactory();
        this.reportFactory = new ReportFactory();
    }

    public Report processMission(Path file, ReportType reportType) throws MissionParsingException {

        // Выбираем парсер через Factory
        MissionParser parser = parserFactory.getParser(file);

        // Парсим миссию
        Mission mission = parser.parse(file);

        // Генерируем отчет через Factory/Strategy
        MissionReporter reporter = reportFactory.createReporter(reportType);
        return reporter.generate(mission);
    }

    public Report processMission(Path file) throws MissionParsingException {
        return processMission(file, ReportType.DETAILED);
    }
}