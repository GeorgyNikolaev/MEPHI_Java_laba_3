package persistence.entity;

import enums.ThreatLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_curses")
public class CurseEntity {

    @Id
    private Long missionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "mission_id")
    private MissionEntity mission;

    @Column(nullable = false, length = 512)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "threat_level", nullable = false, length = 32)
    private ThreatLevel threatLevel;

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
}
