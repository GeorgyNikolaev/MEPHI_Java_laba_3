package persistence.entity;

import enums.TimeOfDay;
import enums.VisibilityLevel;
import enums.WeatherCondition;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_environment_conditions")
public class EnvironmentConditionsEntity {

    @Id
    private Long missionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "mission_id")
    private MissionEntity mission;

    @Enumerated(EnumType.STRING)
    @Column(length = 64)
    private WeatherCondition weather;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_of_day", length = 64)
    private TimeOfDay timeOfDay;

    @Enumerated(EnumType.STRING)
    @Column(length = 64)
    private VisibilityLevel visibility;

    @Column(name = "cursed_energy_density")
    private Double cursedEnergyDensity;

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

    public WeatherCondition getWeather() {
        return weather;
    }

    public void setWeather(WeatherCondition weather) {
        this.weather = weather;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public VisibilityLevel getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityLevel visibility) {
        this.visibility = visibility;
    }

    public Double getCursedEnergyDensity() {
        return cursedEnergyDensity;
    }

    public void setCursedEnergyDensity(Double cursedEnergyDensity) {
        this.cursedEnergyDensity = cursedEnergyDensity;
    }
}
