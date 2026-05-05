package enums;

public enum VisibilityLevel {
    EXCELLENT("Отличная"),
    GOOD("Хорошая"),
    MODERATE("Умеренная"),
    POOR("Плохая"),
    LOW("Очень плохая"),
    ZERO("Нулевая"),
    UNKNOWN("Неизвестно");

    private final String description;

    VisibilityLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static VisibilityLevel fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return VisibilityLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}