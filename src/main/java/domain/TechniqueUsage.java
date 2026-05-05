package domain;

public class TechniqueUsage {
    Technique technique;
    Sorcerer owner;
    Long damage;

    public TechniqueUsage(Technique technique, Sorcerer owner, Long damage) {
        this.technique = technique;
        this.owner = owner;
        this.damage = damage;
    }

    public TechniqueUsage() {

    }

    public Technique getTechnique() {
        return technique;
    }

    public void setTechnique(Technique technique) {
        this.technique = technique;
    }

    public Sorcerer getOwner() {
        return owner;
    }

    public void setOwner(Sorcerer owner) {
        this.owner = owner;
    }

    public Long getDamage() {
        return damage;
    }

    public void setDamage(Long damage) {
        this.damage = damage;
    }

    @Override
    public String toString() {
        return "TechniqueUsage{" +
                "technique=" + technique +
                ", owner=" + (owner != null ? owner.getName() : "null") +
                ", damage=" + damage +
                '}';
    }
}