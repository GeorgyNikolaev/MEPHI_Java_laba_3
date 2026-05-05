package domain;

import java.util.Objects;


public class CivilianImpact {

    private Integer evacuated;
    private Integer injured;
    private Integer missing;

    public CivilianImpact() {}

    public Integer getEvacuated() { return evacuated; }
    public void setEvacuated(Integer evacuated) { this.evacuated = evacuated; }

    public Integer getInjured() { return injured; }
    public void setInjured(Integer injured) { this.injured = injured; }

    public Integer getMissing() { return missing; }
    public void setMissing(Integer missing) { this.missing = missing; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CivilianImpact)) return false;
        CivilianImpact that = (CivilianImpact) o;
        return Objects.equals(evacuated, that.evacuated) &&
                Objects.equals(injured, that.injured) &&
                Objects.equals(missing, that.missing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evacuated, injured, missing);
    }

    @Override
    public String toString() {
        return "CivilianImpact{" +
                "evacuated=" + evacuated +
                ", injured=" + injured +
                ", missing=" + missing +
                '}';
    }
}