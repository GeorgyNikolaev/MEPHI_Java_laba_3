package enums;

public enum ReportType {
    SUMMARY("Краткий отчет"),
    DETAILED("Детальный отчет"),
    RISK("Отчет по рискам"),
    STATISTICAL("Статистический отчет");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}