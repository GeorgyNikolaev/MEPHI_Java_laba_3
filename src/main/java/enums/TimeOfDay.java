package enums;

public enum TimeOfDay {
    DAWN("Рассвет"),
    MORNING("Утро"),
    NOON("Полдень"),
    AFTERNOON("День"),
    EVENING("Вечер"),
    NIGHT("Ночь"),
    MIDNIGHT("Полночь"),
    UNKNOWN("Неизвестно");

    private final String description;

    TimeOfDay(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static TimeOfDay fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return TimeOfDay.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}