package enums;

public enum MobilityLevel {
    LOW("Низкая"),
    MEDIUM("Средняя"),
    HIGH("Высокая"),
    VARIABLE("Переменная"),
    UNKNOWN("Неизвестно");

    private final String description;

    MobilityLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static MobilityLevel fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return MobilityLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}