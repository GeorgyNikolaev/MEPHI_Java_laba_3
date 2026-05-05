package reporter;

import enums.ReportType;

public class Report {
    private String content;
    private ReportType type;

    public Report(String content, ReportType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public ReportType getType() {
        return type;
    }
}