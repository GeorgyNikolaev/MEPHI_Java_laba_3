package enums;

public enum WeatherCondition {
    CLEAR("Ясно"),
    CLOUDY("Облачно"),
    RAIN("Дождь"),
    HEAVY_RAIN("Сильный дождь"),
    SNOW("Снег"),
    FOG("Туман"),
    STORM("Шторм"),
    UNKNOWN("Неизвестно");

    private final String description;

    WeatherCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static WeatherCondition fromString(String value) {
        if (value == null) return UNKNOWN;
        try {
            return WeatherCondition.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}