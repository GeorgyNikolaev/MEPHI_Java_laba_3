package enums;

public enum EscalationRisk {
    LOW("Низкий"),
    MEDIUM("Средний"),
    HIGH("Высокий"),
    CRITICAL("Критический"),
    UNKNOWN("Неизвестно");

    private final String description;

    EscalationRisk(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static EscalationRisk fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return EscalationRisk.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}