package domain;

import enums.ThreatLevel;

public class Curse {
    String name;
    ThreatLevel threatLevel;

    public Curse(String name, ThreatLevel threatLevel) {
        this.name = name;
        this.threatLevel = threatLevel;
    }

    public Curse() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThreatLevel getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(ThreatLevel threatLevel) {
        this.threatLevel = threatLevel;
    }

    @Override
    public String toString() {
        return "Curse{" +
                "name='" + name + '\'' +
                ", threatLevel=" + threatLevel +
                '}';
    }
}