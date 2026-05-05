package domain;

import enums.TechniqueType;

public class Technique {
    String name;
    TechniqueType type;


    public Technique(String name, TechniqueType type) {
        this.name = name;
        this.type = type;
    }

    public Technique() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TechniqueType getType() {
        return type;
    }

    public void setType(TechniqueType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Technique{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}