package service;

import domain.*;
import enums.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import persistence.entity.MissionEntity;
import persistence.entity.SorcererEntity;
import support.MissionTestFixtures;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MissionMapperTest {

    @Mock
    private EntityManager entityManager;

    private MissionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MissionMapper();
        ReflectionTestUtils.setField(mapper, "entityManager", entityManager);
    }

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

    @Test
    void newEntityFromDomain_assignsSequentialPosToChildren() {
        Mission source = MissionTestFixtures.sampleMission();

        MissionEntity entity = mapper.newEntityFromDomain(source, "fixture.json");

        assertEquals(0, entity.getSorcerers().get(0).getPos());
        assertEquals(0, entity.getTechniques().get(0).getPos());
    }

    @Test
    void replaceMissionPayload_replacesNestedDataAndFlushesSession() {
        MissionEntity existing = mapper.newEntityFromDomain(MissionTestFixtures.sampleMission(), "old.json");
        existing.setId(1L);
        for (SorcererEntity s : existing.getSorcerers()) {
            s.setId(10L);
        }

        Mission updated = new Mission();
        updated.setMissionId("M-UPDATED");
        updated.setDate(LocalDate.of(2025, 1, 1));
        updated.setLocation("Osaka");
        updated.setOutcome(MissionOutcome.FAILURE);
        Curse curse = new Curse();
        curse.setName("New curse");
        curse.setThreatLevel(ThreatLevel.LOW);
        updated.setCurse(curse);

        mapper.replaceMissionPayload(existing, updated, "new.json");

        verify(entityManager).flush();
        assertEquals("M-UPDATED", existing.getMissionCode());
        assertEquals("Osaka", existing.getLocation());
        assertEquals(MissionOutcome.FAILURE, existing.getOutcome());
        assertEquals("new.json", existing.getSourceFilename());
        assertNotNull(existing.getCurse());
        assertEquals("New curse", existing.getCurse().getName());
        assertTrue(existing.getSorcerers().isEmpty());
    }

    @Test
    void toDomain_sortsChildrenByPos() {
        MissionEntity entity = mapper.newEntityFromDomain(MissionTestFixtures.sampleMission(), "f.json");
        entity.getSorcerers().get(0).setPos(1);
        entity.getSorcerers().add(new SorcererEntity());
        entity.getSorcerers().get(1).setMission(entity);
        entity.getSorcerers().get(1).setPos(0);
        entity.getSorcerers().get(1).setName("Zeta");
        entity.getSorcerers().get(1).setRank(SorcererRank.GRADE_2);

        Mission restored = mapper.toDomain(entity);

        assertEquals("Zeta", restored.getSorcerers().get(0).getName());
        assertEquals("Alice", restored.getSorcerers().get(1).getName());
    }
}
