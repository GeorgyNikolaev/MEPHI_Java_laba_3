package service;

import domain.*;
import enums.*;
import org.junit.jupiter.api.Test;
import persistence.entity.MissionEntity;
import persistence.entity.SorcererEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MissionMapperTest {

    private final MissionMapper mapper = new MissionMapper();

    @Test
    void toDomain_roundTrip_preservesCoreFields() {
        Mission source = new Mission();
        source.setMissionId("M-TEST-001");
        source.setDate(LocalDate.of(2024, 5, 1));
        source.setLocation("Tokyo");
        source.setOutcome(MissionOutcome.SUCCESS);
        source.setDamageCost(1000L);

        Curse curse = new Curse();
        curse.setName("Test curse");
        curse.setThreatLevel(ThreatLevel.HIGH);
        source.setCurse(curse);

        Sorcerer s1 = new Sorcerer();
        s1.setName("Alice");
        s1.setRank(SorcererRank.GRADE_1);
        source.getSorcerers().add(s1);

        Technique t = new Technique();
        t.setName("Flash");
        t.setType(TechniqueType.INNATE);
        TechniqueUsage tu = new TechniqueUsage();
        tu.setTechnique(t);
        tu.setOwner(s1);
        tu.setDamage(500L);
        source.getTechniques().add(tu);

        MissionEntity entity = mapper.newEntityFromDomain(source, "demo.json");
        entity.setId(42L);
        assertEquals(0, entity.getSorcerers().get(0).getPos());
        assertEquals(0, entity.getTechniques().get(0).getPos());
        long id = 1L;
        for (SorcererEntity se : entity.getSorcerers()) {
            se.setId(id++);
        }

        Mission restored = mapper.toDomain(entity);

        assertEquals("M-TEST-001", restored.getMissionId());
        assertEquals(LocalDate.of(2024, 5, 1), restored.getDate());
        assertEquals("Tokyo", restored.getLocation());
        assertEquals(MissionOutcome.SUCCESS, restored.getOutcome());
        assertEquals(1000L, restored.getDamageCost());
        assertNotNull(restored.getCurse());
        assertEquals("Test curse", restored.getCurse().getName());
        assertEquals(ThreatLevel.HIGH, restored.getCurse().getThreatLevel());
        assertEquals(1, restored.getSorcerers().size());
        assertEquals("Alice", restored.getSorcerers().get(0).getName());
        assertEquals(1, restored.getTechniques().size());
        assertEquals("Flash", restored.getTechniques().get(0).getTechnique().getName());
        assertEquals("Alice", restored.getTechniques().get(0).getOwner().getName());
        assertEquals(500L, restored.getTechniques().get(0).getDamage());
    }
}
