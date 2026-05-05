package domain;

import enums.TimeOfDay;
import enums.VisibilityLevel;
import enums.WeatherCondition;

import java.util.Objects;


public class EnvironmentConditions {

    private WeatherCondition weather;
    private TimeOfDay timeOfDay;
    private VisibilityLevel visibility;
    private Double cursedEnergyDensity;

    public EnvironmentConditions() {}

    public WeatherCondition getWeather() { return weather; }
    public void setWeather(WeatherCondition weather) { this.weather = weather; }

    public TimeOfDay getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(TimeOfDay timeOfDay) { this.timeOfDay = timeOfDay; }

    public VisibilityLevel getVisibility() { return visibility; }
    public void setVisibility(VisibilityLevel visibility) { this.visibility = visibility; }

    public Double getCursedEnergyDensity() { return cursedEnergyDensity; }
    public void setCursedEnergyDensity(Double cursedEnergyDensity) {
        this.cursedEnergyDensity = cursedEnergyDensity;
    }

    // Utility methods
    public boolean isLowVisibility() {
        return visibility == VisibilityLevel.POOR ||
                visibility == VisibilityLevel.LOW ||
                visibility == VisibilityLevel.ZERO;
    }

    public boolean isHighCursedEnergy() {
        return cursedEnergyDensity != null && cursedEnergyDensity > 80.0;
    }

    public boolean isNightOperation() {
        return timeOfDay == TimeOfDay.NIGHT || timeOfDay == TimeOfDay.MIDNIGHT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnvironmentConditions)) return false;
        EnvironmentConditions that = (EnvironmentConditions) o;
        return weather == that.weather &&
                timeOfDay == that.timeOfDay &&
                visibility == that.visibility &&
                Objects.equals(cursedEnergyDensity, that.cursedEnergyDensity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weather, timeOfDay, visibility, cursedEnergyDensity);
    }

    @Override
    public String toString() {
        return "EnvironmentConditions{" +
                "weather=" + weather +
                ", timeOfDay=" + timeOfDay +
                ", visibility=" + visibility +
                ", cursedEnergyDensity=" + cursedEnergyDensity +
                '}';
    }
}