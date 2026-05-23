package support;

import domain.Curse;
import domain.Mission;
import domain.Sorcerer;
import domain.Technique;
import domain.TechniqueUsage;
import enums.MissionOutcome;
import enums.SorcererRank;
import enums.TechniqueType;
import enums.ThreatLevel;
import persistence.entity.MissionEntity;

import java.time.Instant;
import java.time.LocalDate;

public final class MissionTestFixtures {

    private MissionTestFixtures() {
    }

    public static Mission sampleMission() {
        Mission mission = new Mission();
        mission.setMissionId("M-FIXTURE-01");
        mission.setDate(LocalDate.of(2024, 10, 12));
        mission.setLocation("Tokyo");
        mission.setOutcome(MissionOutcome.SUCCESS);
        mission.setDamageCost(1000L);

        Curse curse = new Curse();
        curse.setName("Fixture curse");
        curse.setThreatLevel(ThreatLevel.HIGH);
        mission.setCurse(curse);

        Sorcerer s1 = new Sorcerer();
        s1.setName("Alice");
        s1.setRank(SorcererRank.GRADE_1);
        mission.getSorcerers().add(s1);

        Technique t = new Technique();
        t.setName("Flash");
        t.setType(TechniqueType.INNATE);
        TechniqueUsage tu = new TechniqueUsage();
        tu.setTechnique(t);
        tu.setOwner(s1);
        tu.setDamage(500L);
        mission.getTechniques().add(tu);

        return mission;
    }

    public static MissionEntity sampleEntity(Long id) {
        MissionEntity entity = new MissionEntity();
        entity.setId(id);
        entity.setMissionCode("M-FIXTURE-01");
        entity.setOperationDate(LocalDate.of(2024, 10, 12));
        entity.setLocation("Tokyo");
        entity.setOutcome(MissionOutcome.SUCCESS);
        entity.setDamageCost(1000L);
        entity.setSourceFilename("fixture.json");
        entity.setCreatedAt(Instant.parse("2024-10-12T10:00:00Z"));
        return entity;
    }
}
