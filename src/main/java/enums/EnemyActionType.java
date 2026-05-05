package enums;

public enum EnemyActionType {
    DIRECT_ASSAULT("Прямое нападение"),
    TRAP_USAGE("Ловушка"),
    UNKNOWN("Неизвестно");

    private final String description;

    EnemyActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static EnemyActionType fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return EnemyActionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
