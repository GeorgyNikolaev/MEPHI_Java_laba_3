package persistence.entity;

import enums.MissionOutcome;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missions")
public class MissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_code", nullable = false, unique = true, length = 128)
    private String missionCode;

    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

    @Column(nullable = false, length = 512)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MissionOutcome outcome;

    @Column(name = "damage_cost")
    private Long damageCost;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "source_filename", length = 512)
    private String sourceFilename;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private CurseEntity curse;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SorcererEntity> sorcerers = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechniqueUsageEntity> techniques = new ArrayList<>();

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnemyActivityEntity enemyActivity;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnemyActionEntity> enemyActions = new ArrayList<>();

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private EconomicAssessmentEntity economicAssessment;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private CivilianImpactEntity civilianImpact;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnvironmentConditionsEntity environmentConditions;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperationTimelineEntity> operationTimeline = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_operation_tags", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "tag", length = 512)
    private List<String> operationTags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_support_units", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "unit", length = 512)
    private List<String> supportUnits = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_recommendations", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "text", length = 1024)
    private List<String> recommendations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_notes", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "text", length = 2048)
    private List<String> notes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_artifacts_recovered", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "artifact", length = 512)
    private List<String> artifactsRecovered = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_evacuation_zones", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "zone", length = 512)
    private List<String> evacuationZones = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_status_effects", joinColumns = @JoinColumn(name = "mission_id"))
    @Column(name = "effect", length = 512)
    private List<String> statusEffects = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionReportEntity> reports = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public LocalDate getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDate operationDate) {
        this.operationDate = operationDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public MissionOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(MissionOutcome outcome) {
        this.outcome = outcome;
    }

    public Long getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(Long damageCost) {
        this.damageCost = damageCost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public CurseEntity getCurse() {
        return curse;
    }

    public void setCurse(CurseEntity curse) {
        this.curse = curse;
    }

    public List<SorcererEntity> getSorcerers() {
        return sorcerers;
    }

    public void setSorcerers(List<SorcererEntity> sorcerers) {
        this.sorcerers = sorcerers;
    }

    public List<TechniqueUsageEntity> getTechniques() {
        return techniques;
    }

    public void setTechniques(List<TechniqueUsageEntity> techniques) {
        this.techniques = techniques;
    }

    public EnemyActivityEntity getEnemyActivity() {
        return enemyActivity;
    }

    public void setEnemyActivity(EnemyActivityEntity enemyActivity) {
        this.enemyActivity = enemyActivity;
    }

    public List<EnemyActionEntity> getEnemyActions() {
        return enemyActions;
    }

    public void setEnemyActions(List<EnemyActionEntity> enemyActions) {
        this.enemyActions = enemyActions;
    }

    public EconomicAssessmentEntity getEconomicAssessment() {
        return economicAssessment;
    }

    public void setEconomicAssessment(EconomicAssessmentEntity economicAssessment) {
        this.economicAssessment = economicAssessment;
    }

    public CivilianImpactEntity getCivilianImpact() {
        return civilianImpact;
    }

    public void setCivilianImpact(CivilianImpactEntity civilianImpact) {
        this.civilianImpact = civilianImpact;
    }

    public EnvironmentConditionsEntity getEnvironmentConditions() {
        return environmentConditions;
    }

    public void setEnvironmentConditions(EnvironmentConditionsEntity environmentConditions) {
        this.environmentConditions = environmentConditions;
    }

    public List<OperationTimelineEntity> getOperationTimeline() {
        return operationTimeline;
    }

    public void setOperationTimeline(List<OperationTimelineEntity> operationTimeline) {
        this.operationTimeline = operationTimeline;
    }

    public List<String> getOperationTags() {
        return operationTags;
    }

    public void setOperationTags(List<String> operationTags) {
        this.operationTags = operationTags;
    }

    public List<String> getSupportUnits() {
        return supportUnits;
    }

    public void setSupportUnits(List<String> supportUnits) {
        this.supportUnits = supportUnits;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public List<String> getArtifactsRecovered() {
        return artifactsRecovered;
    }

    public void setArtifactsRecovered(List<String> artifactsRecovered) {
        this.artifactsRecovered = artifactsRecovered;
    }

    public List<String> getEvacuationZones() {
        return evacuationZones;
    }

    public void setEvacuationZones(List<String> evacuationZones) {
        this.evacuationZones = evacuationZones;
    }

    public List<String> getStatusEffects() {
        return statusEffects;
    }

    public void setStatusEffects(List<String> statusEffects) {
        this.statusEffects = statusEffects;
    }

    public List<MissionReportEntity> getReports() {
        return reports;
    }

    public void setReports(List<MissionReportEntity> reports) {
        this.reports = reports;
    }
}
