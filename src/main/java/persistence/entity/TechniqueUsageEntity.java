package persistence.entity;

import enums.TechniqueType;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_technique_usages")
public class TechniqueUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private MissionEntity mission;

    @Column(name = "pos", nullable = false)
    private int pos;

    @Column(name = "technique_name", nullable = false, length = 256)
    private String techniqueName;

    @Enumerated(EnumType.STRING)
    @Column(name = "technique_type", nullable = false, length = 32)
    private TechniqueType techniqueType;

    private Long damage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_sorcerer_id")
    private SorcererEntity owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MissionEntity getMission() {
        return mission;
    }

    public void setMission(MissionEntity mission) {
        this.mission = mission;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getTechniqueName() {
        return techniqueName;
    }

    public void setTechniqueName(String techniqueName) {
        this.techniqueName = techniqueName;
    }

    public TechniqueType getTechniqueType() {
        return techniqueType;
    }

    public void setTechniqueType(TechniqueType techniqueType) {
        this.techniqueType = techniqueType;
    }

    public Long getDamage() {
        return damage;
    }

    public void setDamage(Long damage) {
        this.damage = damage;
    }

    public SorcererEntity getOwner() {
        return owner;
    }

    public void setOwner(SorcererEntity owner) {
        this.owner = owner;
    }
}
