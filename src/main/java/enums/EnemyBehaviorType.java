package enums;

public enum EnemyBehaviorType {
    AMBUSH_PREDATOR("Засадный хищник"),
    SWARM_ATTACKER("Атакующий роем"),
    PSYCHOLOGICAL_WARFARE("Психологическая война"),
    HIT_AND_RUN("Ударил-убежал"),
    STRATEGIC_DEFENDER("Стратегическая оборона"),
    UNKNOWN("Неизвестно");

    private final String description;

    EnemyBehaviorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static EnemyBehaviorType fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return EnemyBehaviorType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}