package enums;


public enum OperationTimelineType {
    DEPLOYMENT("Развертывание"),
    ENGAGEMENT("Контакт с противником"),
    TECHNIQUE_USED("Применение техники"),
    CASUALTY("Потери"),
    EVACUATION("Эвакуация"),
    CIVILIAN_EVACUATION("Эвакуация гражданских"),
    CONTAINMENT("Сдерживание"),
    EXFILTRATION("Завершение операции"),
    DETECTION("Обнаружение"),
    UNKNOWN("Неизвестно");

    private final String description;

    OperationTimelineType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static OperationTimelineType fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return OperationTimelineType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}