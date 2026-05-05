package domain;

import enums.EnemyActionType;

public class EnemyAction {
    private EnemyActionType type;
    private String name;

    public EnemyAction() {

    }

    public EnemyActionType getType() {
        return type;
    }

    public void setType(EnemyActionType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnemyAction(EnemyActionType type, String name) {
        this.type = type;
        this.name = name;
    }
}
