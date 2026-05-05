package domain;

import enums.EnemyBehaviorType;
import enums.EscalationRisk;
import enums.MobilityLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EnemyActivity {

    private EnemyBehaviorType behaviorType;
    private List<String> targetPriority;
    private MobilityLevel mobility;
    private EscalationRisk escalationRisk;
    private List<String> attackPatterns;
    private List<String> countermeasuresUsed;

    public EnemyActivity() {
        this.targetPriority = new ArrayList<>();
        this.attackPatterns = new ArrayList<>();
        this.countermeasuresUsed = new ArrayList<>();
    }

    public EnemyBehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(EnemyBehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    public List<String> getTargetPriority() {
        return targetPriority;
    }

    public void setTargetPriority(List<String> targetPriority) {
        this.targetPriority = targetPriority != null ? targetPriority : new ArrayList<>();
    }

    public void addTargetPriority(String priority) {
        if (priority != null && !priority.trim().isEmpty()) {
            this.targetPriority.add(priority.trim());
        }
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

    public List<String> getAttackPatterns() {
        return attackPatterns;
    }

    public void setAttackPatterns(List<String> attackPatterns) {
        this.attackPatterns = attackPatterns != null ? attackPatterns : new ArrayList<>();
    }

    public void addAttackPattern(String pattern) {
        if (pattern != null && !pattern.trim().isEmpty()) {
            this.attackPatterns.add(pattern.trim());
        }
    }

    public List<String> getCountermeasuresUsed() {
        return countermeasuresUsed;
    }

    public void setCountermeasuresUsed(List<String> countermeasuresUsed) {
        this.countermeasuresUsed = countermeasuresUsed != null ? countermeasuresUsed : new ArrayList<>();
    }

    public void addCountermeasure(String measure) {
        if (measure != null && !measure.trim().isEmpty()) {
            this.countermeasuresUsed.add(measure.trim());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnemyActivity)) return false;
        EnemyActivity that = (EnemyActivity) o;
        return behaviorType == that.behaviorType &&
                mobility == that.mobility &&
                escalationRisk == that.escalationRisk &&
                Objects.equals(targetPriority, that.targetPriority) &&
                Objects.equals(attackPatterns, that.attackPatterns) &&
                Objects.equals(countermeasuresUsed, that.countermeasuresUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behaviorType, targetPriority, mobility,
                escalationRisk, attackPatterns, countermeasuresUsed);
    }

    @Override
    public String toString() {
        return "EnemyActivity{" +
                "behaviorType=" + behaviorType +
                ", mobility=" + mobility +
                ", escalationRisk=" + escalationRisk +
                ", attackPatternsCount=" + attackPatterns.size() +
                ", countermeasuresCount=" + countermeasuresUsed.size() +
                '}';
    }
}
