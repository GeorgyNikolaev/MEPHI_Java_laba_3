package parser;

import domain.*;
import enums.*;
import exception.MissionParsingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionConverter {

    public static Mission convertToMission(Map<String, Object> data) throws MissionParsingException {
        // 2. Создание миссии
        Mission mission = new Mission();

        // Заполнение основных полей
        mission.setMissionId(getString(data, "missionId", true));
        mission.setDate(getDate(data, "date", true));
        mission.setLocation(getString(data, "location", true));
        mission.setOutcome(getOutcome(data, "outcome", true));
        mission.setDamageCost(getLong(data, "damageCost", false));
        mission.setComment(getString(data, "comment", false));

        // 4. Проклятие (обязательный блок)
        Curse curse = parseCurse(data);
        mission.setCurse(curse);

        // 5. Маги (опциональный список)
        List<Sorcerer> sorcerers = parseSorcerers(data);
        mission.setSorcerers(sorcerers);

        // 6. Техники (опциональный список)
        List<TechniqueUsage> techniques = parseTechniques(data, sorcerers);
        mission.setTechniques(techniques);

        EnemyActivity enemyActivity = parseEnemyActivity(data);
        mission.setEnemyActivity(enemyActivity);

        List<EnemyAction> enemyActions = parseEnemyActions(data);
        mission.setEnemyActions(enemyActions);

        EconomicAssessment economicAssessment = parseEconomicAssessment(data);
        mission.setEconomicAssessment(economicAssessment);

        CivilianImpact civilianImpact = parseCivilianImpact(data);
        mission.setCivilianImpact(civilianImpact);

        EnvironmentConditions environmentConditions = parseEnvironmentConditions(data);
        mission.setEnvironmentConditions(environmentConditions);

        List<OperationTimeline> operationTimeline = parseOperationTimeline(data);
        mission.setOperationTimeline(operationTimeline);

        return mission;
    }

    private static Curse parseCurse(Map<String, Object> data)
            throws MissionParsingException {

        Map<String, Object> curseData = getMap(data, "curse");
        if (curseData == null) {
            throw new MissionParsingException("Отсутствует блок curse");
        }

        Curse curse = new Curse();
        curse.setName(getString(curseData, "name", true));
        curse.setThreatLevel(getThreatLevel(curseData, "threatLevel", true));

        return curse;
    }

    @SuppressWarnings("unchecked")
    private static List<Sorcerer> parseSorcerers(Map<String, Object> data) throws MissionParsingException {
        List<Sorcerer> sorcerers = new ArrayList<>();

        Object sorcerersObj = getWithPluralFallback(data, "sorcerer");
        if (sorcerersObj instanceof List) {
            List<Map<String, Object>> sorcerersList = (List<Map<String, Object>>) sorcerersObj;

            for (Map<String, Object> sorcererData : sorcerersList) {
                Sorcerer sorcerer = new Sorcerer();
                sorcerer.setName(getString(sorcererData, "name", true));
                sorcerer.setRank(getSorcererRank(sorcererData, "rank", true));
                sorcerers.add(sorcerer);
            }
        }

        return sorcerers;
    }

    @SuppressWarnings("unchecked")
    private static EnemyActivity parseEnemyActivity(Map<String, Object> data) throws MissionParsingException {
        Map<String, Object> enemyData = getMap(data, "enemyActivity");
        if (enemyData == null) return null;

        EnemyActivity activity = new EnemyActivity();

        // Простые поля
        String behaviorTypeRaw = getString(enemyData, "behaviorType", false);
        if (behaviorTypeRaw != null) {
            activity.setBehaviorType(EnemyBehaviorType.fromString(behaviorTypeRaw));
        }

        String mobilityRaw = getString(enemyData, "mobility", false);
        if (mobilityRaw != null) {
            activity.setMobility(MobilityLevel.fromString(mobilityRaw));
        }

        String escalationRaw = getString(enemyData, "escalationRisk", false);
        if (escalationRaw != null) {
            activity.setEscalationRisk(EscalationRisk.fromString(escalationRaw));
        }

        // targetPriority - может быть строкой или списком
        Object targetObj = enemyData.get("targetPriority");
        if (targetObj instanceof String) {
            activity.addTargetPriority((String) targetObj);
        } else if (targetObj instanceof List) {
            for (Object item : (List<?>) targetObj) {
                if (item instanceof String) {
                    activity.addTargetPriority((String) item);
                }
            }
        }

        // attackPatterns - список строк
        Object patternsObj = enemyData.get("attackPatterns");
        if (patternsObj instanceof List) {
            for (Object item : (List<?>) patternsObj) {
                if (item instanceof String) {
                    activity.addAttackPattern((String) item);
                }
            }
        }

        // countermeasuresUsed - список строк
        Object measuresObj = enemyData.get("countermeasuresUsed");
        if (measuresObj instanceof List) {
            for (Object item : (List<?>) measuresObj) {
                if (item instanceof String) {
                    activity.addCountermeasure((String) item);
                }
            }
        }

        return activity;
    }

    @SuppressWarnings("unchecked")
    private static List<TechniqueUsage> parseTechniques(
            Map<String, Object> data,
            List<Sorcerer> sorcerers) throws MissionParsingException {

        List<TechniqueUsage> techniques = new ArrayList<>();

        Object techniquesObj = getWithPluralFallback(data, "technique");
        if (techniquesObj instanceof List) {
            List<Map<String, Object>> techniquesList = (List<Map<String, Object>>) techniquesObj;

            for (Map<String, Object> techData : techniquesList) {
                Technique technique = new Technique();
                technique.setName(getString(techData, "name", true));
                technique.setType(getTechniqueType(techData, "type", true));

                TechniqueUsage usage = new TechniqueUsage();
                usage.setTechnique(technique);

                // Владелец техники
                String ownerName = getString(techData, "owner", true);
                if (ownerName != null && !sorcerers.isEmpty()) {
                    Sorcerer owner = findSorcererByName(sorcerers, ownerName);
                    usage.setOwner(owner);
                }

                // Урон
                usage.setDamage(getLong(techData, "damage", false));

                techniques.add(usage);
            }
        }

        return techniques;
    }

    @SuppressWarnings("unchecked")
    private static List<EnemyAction> parseEnemyActions(
            Map<String, Object> data) throws MissionParsingException {

        List<EnemyAction> enemyActions = new ArrayList<>();

        Object enemyActionsObj = getWithPluralFallback(data, "enemyActions");
        if (enemyActionsObj instanceof List) {
            List<Map<String, Object>> enemyActionsList = (List<Map<String, Object>>) enemyActionsObj;

            for (Map<String, Object> enemyActionData : enemyActionsList) {
                EnemyAction enemyAction = new EnemyAction();
                enemyAction.setType(getEnemyActionType(enemyActionData, "type", true));
                enemyAction.setName(getString(enemyActionData, "name", true));

                enemyActions.add(enemyAction);
            }
        }

        return enemyActions;
    }

    @SuppressWarnings("unchecked")
    private static EconomicAssessment parseEconomicAssessment(Map<String, Object> data) throws MissionParsingException {
        Map<String, Object> econData = getMap(data, "economicAssessment");
        if (econData == null) return null;

        EconomicAssessment assessment = new EconomicAssessment();

        // Числовые поля (могут быть Number или String)
        assessment.setTotalDamageCost(getLong(econData, "totalDamageCost", false));
        assessment.setInfrastructureDamage(getLong(econData, "infrastructureDamage", false));
        assessment.setCommercialDamage(getLong(econData, "commercialDamage", false));
        assessment.setTransportDamage(getLong(econData, "transportDamage", false));
        assessment.setRecoveryEstimateDays(getInteger(econData, "recoveryEstimateDays", false));

        // Boolean поле
        Object insuranceObj = econData.get("insuranceCovered");
        if (insuranceObj instanceof Boolean) {
            assessment.setInsuranceCovered((Boolean) insuranceObj);
        } else if (insuranceObj instanceof String) {
            assessment.setInsuranceCovered(Boolean.parseBoolean((String) insuranceObj));
        }

        return assessment;
    }

    @SuppressWarnings("unchecked")
    private static CivilianImpact parseCivilianImpact(Map<String, Object> data) throws MissionParsingException {
        Map<String, Object> impactData = getMap(data, "civilianImpact");
        if (impactData == null) return null;

        CivilianImpact impact = new CivilianImpact();

        // Числовые поля
        impact.setEvacuated(getInteger(impactData, "evacuated", false));
        impact.setInjured(getInteger(impactData, "injured", false));
        impact.setMissing(getInteger(impactData, "missing", false));

        return impact;
    }

    private static EnvironmentConditions parseEnvironmentConditions(Map<String, Object> data) throws MissionParsingException {
        Map<String, Object> envData = getMap(data, "environment");
        if (envData == null) return null;

        EnvironmentConditions conditions = new EnvironmentConditions();

        // Enum поля
        String weatherRaw = getString(envData, "weather", false);
        if (weatherRaw != null) {
            conditions.setWeather(WeatherCondition.fromString(weatherRaw));
        }

        String timeRaw = getString(envData, "timeOfDay", false);
        if (timeRaw != null) {
            conditions.setTimeOfDay(TimeOfDay.fromString(timeRaw));
        }

        String visibilityRaw = getString(envData, "visibility", false);
        if (visibilityRaw != null) {
            conditions.setVisibility(VisibilityLevel.fromString(visibilityRaw));
        }

        // Числовое поле
        conditions.setCursedEnergyDensity(getDouble(envData, "cursedEnergyDensity", false));

        return conditions;
    }

    @SuppressWarnings("unchecked")
    private static List<OperationTimeline> parseOperationTimeline(Map<String, Object> data) throws MissionParsingException {
        List<OperationTimeline> timeline = new ArrayList<>();

        Object timelineObj = data.get("operationTimeline");
        if (timelineObj == null) return timeline;

        // Может быть один объект или список
        List<Map<String, Object>> events = new ArrayList<>();
        if (timelineObj instanceof Map) {
            events.add((Map<String, Object>) timelineObj);
        } else if (timelineObj instanceof List) {
            for (Object item : (List<?>) timelineObj) {
                if (item instanceof Map) {
                    events.add((Map<String, Object>) item);
                }
            }
        }

        for (Map<String, Object> eventData : events) {
            OperationTimeline event = new OperationTimeline();

            // Timestamp
            String timestampStr = getString(eventData, "timestamp", false);
            if (timestampStr != null) {
                try {
                    // Поддерживаем ISO-8601 и другие форматы
                    event.setTimestamp(parseTimestamp(timestampStr));
                } catch (DateTimeParseException e) {
                    System.err.println("Неверный формат времени: " + timestampStr);
                }
            }

            // Type
            String typeStr = getString(eventData, "type", false);
            if (typeStr != null) {
                event.setType(OperationTimelineType.fromString(typeStr));
            }

            // Description
            event.setDescription(getString(eventData, "description", false));

            timeline.add(event);
        }

        return timeline;
    }

    // ================= Утилиты =================

    /**
     * Получает значение по ключу, пробуя также plural-вариант
     */
    @SuppressWarnings("unchecked")
    private static Object getWithPluralFallback(Map<String, Object> data, String singularKey) {
        // Сначала пробуем как есть
        Object value = data.get(singularKey);
        if (value != null) return value;

        // Пробуем plural-варианты
        String plural = toPlural(singularKey);
        if (!plural.equals(singularKey)) {
            value = data.get(plural);
            if (value != null) return value;
        }

        return null;
    }

    /**
     * Простая эвристика для plural-формы
     */
    private static String toPlural(String singular) {
        if (singular.endsWith("y")) {
            return singular.substring(0, singular.length() - 1) + "ies";
        } else if (singular.endsWith("s") || singular.endsWith("x") || singular.endsWith("z")) {
            return singular + "es";
        } else {
            return singular + "s";
        }
    }

    private static LocalDateTime parseTimestamp(String value) {
        // ISO-8601
        if (value.contains("T")) {
            return LocalDateTime.parse(value.replace("Z", ""));
        }
        // Простой формат "yyyy-MM-dd HH:mm:ss"
        try {
            return LocalDateTime.parse(value.replace(" ", "T"));
        } catch (Exception e) {
            // Дата без времени
            return LocalDateTime.parse(value + "T00:00:00");
        }
    }

    private static Object getNestedValue(Map<String, Object> map, String key) {
        if (key == null || map == null) return null;

        String[] parts = key.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    private static String getString(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        Object value = getNestedValue(data, key);
        if (value == null) {
            if (required) {
                throw new MissionParsingException("Отсутствует обязательное поле: " + key);
            }
            return null;
        }
        return value.toString().trim();
    }

    private static Integer getInteger(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        Object value = getNestedValue(data, key);
        if (value == null) {
            if (required) {
                throw new MissionParsingException("Отсутствует обязательное поле: " + key);
            }
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString().trim());
        } catch (Exception e) {
            if (required) {
                throw new MissionParsingException("Неверный формат числа в поле " + key);
            }
            return null;
        }
    }

    private static Double getDouble(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        Object value = getNestedValue(data, key);
        if (value == null) {
            if (required) {
                throw new MissionParsingException("Отсутствует обязательное поле: " + key);
            }
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString().trim());
        } catch (Exception e) {
            if (required) {
                throw new MissionParsingException("Неверный формат числа в поле " + key);
            }
            return null;
        }
    }

    protected static LocalDate getDate(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        Object value = getNestedValue(data, key);
        if (value == null) {
            if (required) throw new MissionParsingException("Отсутствует обязательное поле: " + key);
            return null;
        }

        // 1. Если уже LocalDate
        if (value instanceof LocalDate) return (LocalDate) value;

        // 2. Если java.util.Date (от SnakeYAML или других библиотек)
        if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }

        // 3. Если Instant
        if (value instanceof java.time.Instant) {
            return ((java.time.Instant) value).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }

        // 4. По умолчанию считаем строкой
        String strVal = value.toString().trim();
        try {
            return LocalDate.parse(strVal);
        } catch (Exception e) {
            if (required) {
                throw new MissionParsingException("Неверный формат даты в поле " + key + ": " + strVal);
            }
            return null;
        }
    }

    private static Long getLong(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        Object value = getNestedValue(data, key);
        if (value == null) {
            if (required) {
                throw new MissionParsingException("Отсутствует обязательное поле: " + key);
            }
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        try {
            return Long.parseLong(value.toString().trim());
        } catch (Exception e) {
            if (required) {
                throw new MissionParsingException("Неверный формат числа в поле " + key);
            }
            return null;
        }
    }

    private static MissionOutcome getOutcome(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        String value = getString(data, key, required);
        if (value == null) return null;

        try {
            return MissionOutcome.valueOf(value.trim());
        } catch (Exception e) {
            throw new MissionParsingException("Неверное значение enum " + key + ": " + value);
        }
    }

    private static ThreatLevel getThreatLevel(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        String value = getString(data, key, required);
        if (value == null) return null;

        try {
            return ThreatLevel.valueOf(value.trim());
        } catch (Exception e) {
            throw new MissionParsingException("Неверное значение ThreatLevel: " + value);
        }
    }

    private static SorcererRank getSorcererRank(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        String value = getString(data, key, required);
        if (value == null) return null;

        try {
            return SorcererRank.valueOf(value.trim());
        } catch (Exception e) {
            throw new MissionParsingException("Неверное значение SorcererRank: " + value);
        }
    }

    private static TechniqueType getTechniqueType(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        String value = getString(data, key, required);
        if (value == null) return null;

        try {
            return TechniqueType.valueOf(value.trim());
        } catch (Exception e) {
            throw new MissionParsingException("Неверное значение TechniqueType: " + value);
        }
    }

    private static EnemyActionType getEnemyActionType(Map<String, Object> data, String key, boolean required)
            throws MissionParsingException {
        String value = getString(data, key, required);
        if (value == null) return null;

        try {
            return EnemyActionType.valueOf(value.trim());
        } catch (Exception e) {
            throw new MissionParsingException("Неверное значение EnemyActionType: " + value);
        }
    }

    private static Sorcerer findSorcererByName(List<Sorcerer> sorcerers, String name) {
        if (name == null || sorcerers == null) return null;
        for (Sorcerer s : sorcerers) {
            if (s.getName() != null && s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
}