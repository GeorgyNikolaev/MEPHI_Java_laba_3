package persistence.entity;

import enums.EnemyBehaviorType;
import enums.EscalationRisk;
import enums.MobilityLevel;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mission_enemy_activities")
public class EnemyActivityEntity {

    @Id
    private Long missionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "mission_id")
    private MissionEntity mission;

    @Enumerated(EnumType.STRING)
    @Column(name = "behavior_type", length = 64)
    private EnemyBehaviorType behaviorType;

    @Enumerated(EnumType.STRING)
    @Column(length = 64)
    private MobilityLevel mobility;

    @Enumerated(EnumType.STRING)
    @Column(name = "escalation_risk", length = 64)
    private EscalationRisk escalationRisk;

    @ElementCollection
    @CollectionTable(
            name = "enemy_activity_target_priority",
            joinColumns = @JoinColumn(name = "enemy_activity_id")
    )
    @Column(name = "value", length = 512)
    private List<String> targetPriority = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "enemy_activity_attack_patterns",
            joinColumns = @JoinColumn(name = "enemy_activity_id")
    )
    @Column(name = "value", length = 512)
    private List<String> attackPatterns = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "enemy_activity_countermeasures",
            joinColumns = @JoinColumn(name = "enemy_activity_id")
    )
    @Column(name = "value", length = 512)
    private List<String> countermeasuresUsed = new ArrayList<>();

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public MissionEntity getMission() {
        return mission;
    }

    public void setMission(MissionEntity mission) {
        this.mission = mission;
    }

    public EnemyBehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(EnemyBehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    public MobilityLevel getMobility() {
        return mobility;
    }

    public void setMobility(MobilityLevel mobility) {
        this.mobility = mobility;
    }

    public EscalationRisk getEscalationRisk() {
        return escalationRisk;
    }

    public void setEscalationRisk(EscalationRisk escalationRisk) {
        this.escalationRisk = escalationRisk;
    }

    public List<String> getTargetPriority() {
        return targetPriority;
    }

    public void setTargetPriority(List<String> targetPriority) {
        this.targetPriority = targetPriority;
    }

    public List<String> getAttackPatterns() {
        return attackPatterns;
    }

    public void setAttackPatterns(List<String> attackPatterns) {
        this.attackPatterns = attackPatterns;
    }

    public List<String> getCountermeasuresUsed() {
        return countermeasuresUsed;
    }

    public void setCountermeasuresUsed(List<String> countermeasuresUsed) {
        this.countermeasuresUsed = countermeasuresUsed;
    }
}
