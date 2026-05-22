package service;

import domain.*;
import persistence.entity.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class MissionMapper {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Comparator<SorcererEntity> BY_SORCERER_POS =
            Comparator.comparingInt(SorcererEntity::getPos);
    private static final Comparator<TechniqueUsageEntity> BY_TECHNIQUE_POS =
            Comparator.comparingInt(TechniqueUsageEntity::getPos);
    private static final Comparator<EnemyActionEntity> BY_ENEMY_ACTION_POS =
            Comparator.comparingInt(EnemyActionEntity::getPos);
    private static final Comparator<OperationTimelineEntity> BY_TIMELINE_POS =
            Comparator.comparingInt(OperationTimelineEntity::getPos);

    public MissionEntity newEntityFromDomain(Mission source, String sourceFilename) {
        MissionEntity target = new MissionEntity();
        applyDomainPayload(target, source, sourceFilename);
        return target;
    }

    /**
     * Полная замена вложенных данных миссии (при повторной загрузке файла с тем же missionId).
     * Сохраняются id миссии и уже созданные отчёты.
     */
    public void replaceMissionPayload(MissionEntity target, Mission source, String sourceFilename) {
        target.getTechniques().clear();
        target.getSorcerers().clear();
        target.getEnemyActions().clear();
        target.getOperationTimeline().clear();

        target.setCurse(null);
        target.setEnemyActivity(null);
        target.setEconomicAssessment(null);
        target.setCivilianImpact(null);
        target.setEnvironmentConditions(null);

        target.getOperationTags().clear();
        target.getSupportUnits().clear();
        target.getRecommendations().clear();
        target.getNotes().clear();
        target.getArtifactsRecovered().clear();
        target.getEvacuationZones().clear();
        target.getStatusEffects().clear();

        // Сохранить сирот и снять старые @MapsId-сущности с id миссии до вставки новых
        entityManager.flush();

        applyDomainPayload(target, source, sourceFilename);
    }

    private void applyDomainPayload(MissionEntity target, Mission source, String sourceFilename) {
        target.setMissionCode(source.getMissionId());
        target.setOperationDate(source.getDate());
        target.setLocation(source.getLocation());
        target.setOutcome(source.getOutcome());
        target.setDamageCost(source.getDamageCost());
        target.setComment(source.getComment());
        target.setSourceFilename(sourceFilename);

        if (source.getCurse() != null) {
            CurseEntity curse = new CurseEntity();
            curse.setMission(target);
            curse.setName(source.getCurse().getName());
            curse.setThreatLevel(source.getCurse().getThreatLevel());
            target.setCurse(curse);
        }

        Map<String, SorcererEntity> byName = new HashMap<>();
        if (source.getSorcerers() != null) {
            int pos = 0;
            for (Sorcerer s : source.getSorcerers()) {
                SorcererEntity se = new SorcererEntity();
                se.setMission(target);
                se.setPos(pos++);
                se.setName(s.getName());
                se.setRank(s.getRank());
                target.getSorcerers().add(se);
                if (s.getName() != null) {
                    byName.put(s.getName(), se);
                }
            }
        }

        if (source.getTechniques() != null) {
            int pos = 0;
            for (TechniqueUsage tu : source.getTechniques()) {
                TechniqueUsageEntity te = new TechniqueUsageEntity();
                te.setMission(target);
                te.setPos(pos++);
                if (tu.getTechnique() != null) {
                    te.setTechniqueName(tu.getTechnique().getName());
                    te.setTechniqueType(tu.getTechnique().getType());
                }
                te.setDamage(tu.getDamage());
                if (tu.getOwner() != null && tu.getOwner().getName() != null) {
                    te.setOwner(byName.get(tu.getOwner().getName()));
                }
                target.getTechniques().add(te);
            }
        }

        if (source.getEnemyActivity() != null) {
            EnemyActivity ea = source.getEnemyActivity();
            EnemyActivityEntity eae = new EnemyActivityEntity();
            eae.setMission(target);
            eae.setBehaviorType(ea.getBehaviorType());
            eae.setMobility(ea.getMobility());
            eae.setEscalationRisk(ea.getEscalationRisk());
            eae.setTargetPriority(new ArrayList<>(ea.getTargetPriority()));
            eae.setAttackPatterns(new ArrayList<>(ea.getAttackPatterns()));
            eae.setCountermeasuresUsed(new ArrayList<>(ea.getCountermeasuresUsed()));
            target.setEnemyActivity(eae);
        }

        if (source.getEnemyActions() != null) {
            int pos = 0;
            for (EnemyAction a : source.getEnemyActions()) {
                EnemyActionEntity ae = new EnemyActionEntity();
                ae.setMission(target);
                ae.setPos(pos++);
                ae.setType(a.getType());
                ae.setName(a.getName());
                target.getEnemyActions().add(ae);
            }
        }

        if (source.getEconomicAssessment() != null) {
            EconomicAssessment src = source.getEconomicAssessment();
            EconomicAssessmentEntity e = new EconomicAssessmentEntity();
            e.setMission(target);
            e.setTotalDamageCost(src.getTotalDamageCost());
            e.setInfrastructureDamage(src.getInfrastructureDamage());
            e.setCommercialDamage(src.getCommercialDamage());
            e.setTransportDamage(src.getTransportDamage());
            e.setRecoveryEstimateDays(src.getRecoveryEstimateDays());
            e.setInsuranceCovered(src.getInsuranceCovered());
            target.setEconomicAssessment(e);
        }

        if (source.getCivilianImpact() != null) {
            CivilianImpact src = source.getCivilianImpact();
            CivilianImpactEntity e = new CivilianImpactEntity();
            e.setMission(target);
            e.setEvacuated(src.getEvacuated());
            e.setInjured(src.getInjured());
            e.setMissing(src.getMissing());
            target.setCivilianImpact(e);
        }

        if (source.getEnvironmentConditions() != null) {
            EnvironmentConditions src = source.getEnvironmentConditions();
            EnvironmentConditionsEntity e = new EnvironmentConditionsEntity();
            e.setMission(target);
            e.setWeather(src.getWeather());
            e.setTimeOfDay(src.getTimeOfDay());
            e.setVisibility(src.getVisibility());
            e.setCursedEnergyDensity(src.getCursedEnergyDensity());
            target.setEnvironmentConditions(e);
        }

        if (source.getOperationTimeline() != null) {
            int pos = 0;
            for (OperationTimeline ot : source.getOperationTimeline()) {
                OperationTimelineEntity e = new OperationTimelineEntity();
                e.setMission(target);
                e.setPos(pos++);
                e.setTimestamp(ot.getTimestamp());
                e.setType(ot.getType());
                e.setDescription(ot.getDescription());
                target.getOperationTimeline().add(e);
            }
        }

        target.getOperationTags().addAll(copyList(source.getOperationTags()));
        target.getSupportUnits().addAll(copyList(source.getSupportUnits()));
        target.getRecommendations().addAll(copyList(source.getRecommendations()));
        target.getNotes().addAll(copyList(source.getNotes()));
        target.getArtifactsRecovered().addAll(copyList(source.getArtifactsRecovered()));
        target.getEvacuationZones().addAll(copyList(source.getEvacuationZones()));
        target.getStatusEffects().addAll(copyList(source.getStatusEffects()));
    }

    public Mission toDomain(MissionEntity e) {
        Mission m = new Mission();
        m.setMissionId(e.getMissionCode());
        m.setDate(e.getOperationDate());
        m.setLocation(e.getLocation());
        m.setOutcome(e.getOutcome());
        m.setDamageCost(e.getDamageCost());
        m.setComment(e.getComment());

        if (e.getCurse() != null) {
            Curse c = new Curse();
            c.setName(e.getCurse().getName());
            c.setThreatLevel(e.getCurse().getThreatLevel());
            m.setCurse(c);
        }

        Map<Long, Sorcerer> sorcererByEntityId = new HashMap<>();
        List<SorcererEntity> sorcerersOrdered = new ArrayList<>(e.getSorcerers());
        sorcerersOrdered.sort(BY_SORCERER_POS);
        for (SorcererEntity se : sorcerersOrdered) {
            Sorcerer s = new Sorcerer();
            s.setName(se.getName());
            s.setRank(se.getRank());
            m.getSorcerers().add(s);
            sorcererByEntityId.put(se.getId(), s);
        }

        List<TechniqueUsageEntity> techniquesOrdered = new ArrayList<>(e.getTechniques());
        techniquesOrdered.sort(BY_TECHNIQUE_POS);
        for (TechniqueUsageEntity te : techniquesOrdered) {
            TechniqueUsage tu = new TechniqueUsage();
            Technique t = new Technique();
            t.setName(te.getTechniqueName());
            t.setType(te.getTechniqueType());
            tu.setTechnique(t);
            tu.setDamage(te.getDamage());
            if (te.getOwner() != null) {
                tu.setOwner(sorcererByEntityId.get(te.getOwner().getId()));
            }
            m.getTechniques().add(tu);
        }

        if (e.getEnemyActivity() != null) {
            EnemyActivityEntity src = e.getEnemyActivity();
            EnemyActivity ea = new EnemyActivity();
            ea.setBehaviorType(src.getBehaviorType());
            ea.setMobility(src.getMobility());
            ea.setEscalationRisk(src.getEscalationRisk());
            ea.setTargetPriority(new ArrayList<>(src.getTargetPriority()));
            ea.setAttackPatterns(new ArrayList<>(src.getAttackPatterns()));
            ea.setCountermeasuresUsed(new ArrayList<>(src.getCountermeasuresUsed()));
            m.setEnemyActivity(ea);
        }

        List<EnemyActionEntity> actionsOrdered = new ArrayList<>(e.getEnemyActions());
        actionsOrdered.sort(BY_ENEMY_ACTION_POS);
        for (EnemyActionEntity ae : actionsOrdered) {
            EnemyAction a = new EnemyAction();
            a.setType(ae.getType());
            a.setName(ae.getName());
            m.getEnemyActions().add(a);
        }

        if (e.getEconomicAssessment() != null) {
            EconomicAssessmentEntity src = e.getEconomicAssessment();
            EconomicAssessment d = new EconomicAssessment();
            d.setTotalDamageCost(src.getTotalDamageCost());
            d.setInfrastructureDamage(src.getInfrastructureDamage());
            d.setCommercialDamage(src.getCommercialDamage());
            d.setTransportDamage(src.getTransportDamage());
            d.setRecoveryEstimateDays(src.getRecoveryEstimateDays());
            d.setInsuranceCovered(src.getInsuranceCovered());
            m.setEconomicAssessment(d);
        }

        if (e.getCivilianImpact() != null) {
            CivilianImpactEntity src = e.getCivilianImpact();
            CivilianImpact d = new CivilianImpact();
            d.setEvacuated(src.getEvacuated());
            d.setInjured(src.getInjured());
            d.setMissing(src.getMissing());
            m.setCivilianImpact(d);
        }

        if (e.getEnvironmentConditions() != null) {
            EnvironmentConditionsEntity src = e.getEnvironmentConditions();
            EnvironmentConditions d = new EnvironmentConditions();
            d.setWeather(src.getWeather());
            d.setTimeOfDay(src.getTimeOfDay());
            d.setVisibility(src.getVisibility());
            d.setCursedEnergyDensity(src.getCursedEnergyDensity());
            m.setEnvironmentConditions(d);
        }

        List<OperationTimelineEntity> timelineOrdered = new ArrayList<>(e.getOperationTimeline());
        timelineOrdered.sort(BY_TIMELINE_POS);
        for (OperationTimelineEntity ot : timelineOrdered) {
            OperationTimeline d = new OperationTimeline();
            d.setTimestamp(ot.getTimestamp());
            d.setType(ot.getType());
            d.setDescription(ot.getDescription());
            m.getOperationTimeline().add(d);
        }

        m.setOperationTags(copyList(e.getOperationTags()));
        m.setSupportUnits(copyList(e.getSupportUnits()));
        m.setRecommendations(copyList(e.getRecommendations()));
        m.setNotes(copyList(e.getNotes()));
        m.setArtifactsRecovered(copyList(e.getArtifactsRecovered()));
        m.setEvacuationZones(copyList(e.getEvacuationZones()));
        m.setStatusEffects(copyList(e.getStatusEffects()));

        return m;
    }

    private static List<String> copyList(List<String> src) {
        return src == null ? new ArrayList<>() : new ArrayList<>(src);
    }
}
