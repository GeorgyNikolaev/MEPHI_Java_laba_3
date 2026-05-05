package domain;

import enums.MissionOutcome;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mission {

    // === Обязательные поля ===
    private String missionId;
    private LocalDate date;
    private String location;
    private MissionOutcome outcome;
    private Curse curse;

    // === Опциональные поля ===
    private Long damageCost;
    private String comment;

    // === Списки ===
    private List<Sorcerer> sorcerers;
    private List<TechniqueUsage> techniques;

    // === Расширяющие блоки (опциональные) ===
    private EconomicAssessment economicAssessment;
    private CivilianImpact civilianImpact;
    private EnemyActivity enemyActivity;  // ← НОВОЕ ПОЛЕ
    private EnvironmentConditions environmentConditions;
    private List<OperationTimeline> operationTimeline;
    private List<EnemyAction> enemyActions;

    // === Метаданные ===
    private List<String> operationTags;
    private List<String> supportUnits;
    private List<String> recommendations;
    private List<String> notes;
    private List<String> artifactsRecovered;
    private List<String> evacuationZones;
    private List<String> statusEffects;

    // Конструктор
    public Mission() {
        this.sorcerers = new ArrayList<>();
        this.techniques = new ArrayList<>();
        this.operationTags = new ArrayList<>();
        this.supportUnits = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.enemyActions = new ArrayList<>();
        this.artifactsRecovered = new ArrayList<>();
        this.evacuationZones = new ArrayList<>();
        this.statusEffects = new ArrayList<>();
        this.operationTimeline = new ArrayList<>();
    }

    // === Getters and Setters для основных полей ===
    public String getMissionId() { return missionId; }
    public void setMissionId(String missionId) { this.missionId = missionId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public MissionOutcome getOutcome() { return outcome; }
    public void setOutcome(MissionOutcome outcome) { this.outcome = outcome; }

    public Curse getCurse() { return curse; }
    public void setCurse(Curse curse) { this.curse = curse; }

    public Long getDamageCost() { return damageCost; }
    public void setDamageCost(Long damageCost) { this.damageCost = damageCost; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    // === Getters and Setters для списков ===
    public List<Sorcerer> getSorcerers() { return sorcerers; }
    public void setSorcerers(List<Sorcerer> sorcerers) {
        this.sorcerers = sorcerers != null ? sorcerers : new ArrayList<>();
    }

    public List<TechniqueUsage> getTechniques() { return techniques; }
    public void setTechniques(List<TechniqueUsage> techniques) {
        this.techniques = techniques != null ? techniques : new ArrayList<>();
    }

    public List<EnemyAction> getEnemyActions() {
        return enemyActions;
    }

    public void setEnemyActions(List<EnemyAction> enemyActions) {
        this.enemyActions = enemyActions;
    }

    // === Getters and Setters для расширяющих блоков ===
    public EconomicAssessment getEconomicAssessment() { return economicAssessment; }
    public void setEconomicAssessment(EconomicAssessment economicAssessment) {
        this.economicAssessment = economicAssessment;
    }

    public CivilianImpact getCivilianImpact() { return civilianImpact; }
    public void setCivilianImpact(CivilianImpact civilianImpact) {
        this.civilianImpact = civilianImpact;
    }

    public EnemyActivity getEnemyActivity() { return enemyActivity; }
    public void setEnemyActivity(EnemyActivity enemyActivity) {
        this.enemyActivity = enemyActivity;
    }

    public EnvironmentConditions getEnvironmentConditions() { return environmentConditions; }
    public void setEnvironmentConditions(EnvironmentConditions environmentConditions) {
        this.environmentConditions = environmentConditions;
    }

    public List<OperationTimeline> getOperationTimeline() { return operationTimeline; }
    public void setOperationTimeline(List<OperationTimeline> operationTimeline) {
        this.operationTimeline = operationTimeline != null ? operationTimeline : new ArrayList<>();
    }

    // === Метаданные ===
    public List<String> getOperationTags() { return operationTags; }
    public void setOperationTags(List<String> operationTags) {
        this.operationTags = operationTags != null ? operationTags : new ArrayList<>();
    }

    public List<String> getSupportUnits() { return supportUnits; }
    public void setSupportUnits(List<String> supportUnits) {
        this.supportUnits = supportUnits != null ? supportUnits : new ArrayList<>();
    }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
    }

    public List<String> getArtifactsRecovered() { return artifactsRecovered; }
    public void setArtifactsRecovered(List<String> artifactsRecovered) {
        this.artifactsRecovered = artifactsRecovered != null ? artifactsRecovered : new ArrayList<>();
    }

    public List<String> getEvacuationZones() { return evacuationZones; }
    public void setEvacuationZones(List<String> evacuationZones) {
        this.evacuationZones = evacuationZones != null ? evacuationZones : new ArrayList<>();
    }

    public List<String> getStatusEffects() { return statusEffects; }
    public void setStatusEffects(List<String> statusEffects) {
        this.statusEffects = statusEffects != null ? statusEffects : new ArrayList<>();
    }

    public boolean hasEnemyActivity() {
        return enemyActivity != null;
    }

    public boolean hasSorcerers() {
        return sorcerers != null && !sorcerers.isEmpty();
    }

    public boolean hasTechniques() {
        return techniques != null && !techniques.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mission)) return false;
        Mission mission = (Mission) o;
        return Objects.equals(missionId, mission.missionId) &&
                Objects.equals(date, mission.date) &&
                Objects.equals(location, mission.location) &&
                outcome == mission.outcome &&
                Objects.equals(curse, mission.curse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, date, location, outcome, curse);
    }

    @Override
    public String toString() {
        return "Mission{" +
                "missionId='" + missionId + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", outcome=" + outcome +
                ", curse=" + curse +
                ", enemyActivity=" + enemyActivity +
                '}';
    }
}