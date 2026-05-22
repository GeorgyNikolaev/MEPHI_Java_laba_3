package persistence.entity;

import enums.EnemyActionType;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_enemy_actions")
public class EnemyActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private MissionEntity mission;

    @Column(name = "pos", nullable = false)
    private int pos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EnemyActionType type;

    @Column(nullable = false, length = 512)
    private String name;

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
}
