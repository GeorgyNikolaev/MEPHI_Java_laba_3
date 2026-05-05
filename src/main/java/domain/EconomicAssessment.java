package domain;

import java.util.Objects;


public class EconomicAssessment {

    private Long totalDamageCost;
    private Long infrastructureDamage;
    private Long commercialDamage;
    private Long transportDamage;
    private Integer recoveryEstimateDays;
    private Boolean insuranceCovered;

    public EconomicAssessment() {}

    public Long getTotalDamageCost() { return totalDamageCost; }
    public void setTotalDamageCost(Long totalDamageCost) { this.totalDamageCost = totalDamageCost; }

    public Long getInfrastructureDamage() { return infrastructureDamage; }
    public void setInfrastructureDamage(Long infrastructureDamage) { this.infrastructureDamage = infrastructureDamage; }

    public Long getCommercialDamage() { return commercialDamage; }
    public void setCommercialDamage(Long commercialDamage) { this.commercialDamage = commercialDamage; }

    public Long getTransportDamage() { return transportDamage; }
    public void setTransportDamage(Long transportDamage) { this.transportDamage = transportDamage; }

    public Integer getRecoveryEstimateDays() { return recoveryEstimateDays; }
    public void setRecoveryEstimateDays(Integer recoveryEstimateDays) { this.recoveryEstimateDays = recoveryEstimateDays; }

    public Boolean getInsuranceCovered() { return insuranceCovered; }
    public void setInsuranceCovered(Boolean insuranceCovered) { this.insuranceCovered = insuranceCovered; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EconomicAssessment)) return false;
        EconomicAssessment that = (EconomicAssessment) o;
        return Objects.equals(totalDamageCost, that.totalDamageCost) &&
                Objects.equals(infrastructureDamage, that.infrastructureDamage) &&
                Objects.equals(commercialDamage, that.commercialDamage) &&
                Objects.equals(transportDamage, that.transportDamage) &&
                Objects.equals(recoveryEstimateDays, that.recoveryEstimateDays) &&
                Objects.equals(insuranceCovered, that.insuranceCovered);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalDamageCost, infrastructureDamage, commercialDamage,
                transportDamage, recoveryEstimateDays, insuranceCovered);
    }

    @Override
    public String toString() {
        return "EconomicAssessment{" +
                "totalDamageCost=" + totalDamageCost +
                ", infrastructureDamage=" + infrastructureDamage +
                ", commercialDamage=" + commercialDamage +
                ", transportDamage=" + transportDamage +
                ", recoveryDays=" + recoveryEstimateDays +
                ", insuranceCovered=" + insuranceCovered +
                '}';
    }
}