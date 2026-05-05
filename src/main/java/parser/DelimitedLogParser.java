package parser;

import exception.MissionParsingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DelimitedLogParser extends MissionParser {

    private static final String DELIMITER = "\\|";

    @Override
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try {
            List<String> lines = Files.readAllLines(file);
            return parseLogToMap(lines);
        } catch (IOException e) {
            throw new MissionParsingException("Ошибка чтения log файла: " + file, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseLogToMap(List<String> lines) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> timeline = new ArrayList<>();
        List<Map<String, String>> sorcerers = new ArrayList<>();
        List<Map<String, String>> techniques = new ArrayList<>();
        List<Map<String, String>> enemyActions = new ArrayList<>();

        Map<String, String> curse = new HashMap<>();
        Map<String, String> civilianImpact = new HashMap<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split(DELIMITER);
            if (parts.length < 2) continue;

            String command = parts[0];

            switch (command) {
                case "MISSION_CREATED" -> parseMissionCreated(result, parts);
                case "CURSE_DETECTED" -> parseCurseDetected(curse, parts);
                case "SORCERER_ASSIGNED" -> sorcerers.add(parseSorcerer(parts));
                case "TECHNIQUE_USED" -> techniques.add(parseTechnique(parts));
                case "TIMELINE_EVENT" -> timeline.add(parseTimelineEvent(parts));
                case "ENEMY_ACTION" -> enemyActions.add(parseEnemyAction(parts));
                case "CIVILIAN_IMPACT" -> parseCivilianImpact(civilianImpact, parts);
                case "MISSION_RESULT" -> parseMissionResult(result, parts);
            }
        }

        // Добавляем собранные блоки
        if (!curse.isEmpty()) result.put("curse", curse);
        if (!sorcerers.isEmpty()) result.put("sorcerers", sorcerers);
        if (!techniques.isEmpty()) result.put("techniques", techniques);
        if (!timeline.isEmpty()) result.put("operationTimeline", timeline);
        if (!enemyActions.isEmpty()) result.put("enemyActions", enemyActions);
        if (!civilianImpact.isEmpty()) result.put("civilianImpact", civilianImpact);

        return result;
    }

    private void parseMissionCreated(Map<String, Object> result, String[] parts) {
        if (parts.length >= 2) result.put("missionId", parts[1]);
        if (parts.length >= 3) result.put("date", parts[2]);
        if (parts.length >= 4) result.put("location", parts[3]);
    }

    private void parseCurseDetected(Map<String, String> curse, String[] parts) {
        if (parts.length >= 2) curse.put("name", parts[1]);
        if (parts.length >= 3) curse.put("threatLevel", parts[2]);
    }

    private Map<String, String> parseSorcerer(String[] parts) {
        Map<String, String> sorcerer = new HashMap<>();
        if (parts.length >= 2) sorcerer.put("name", parts[1]);
        if (parts.length >= 3) sorcerer.put("rank", parts[2]);
        return sorcerer;
    }

    private Map<String, String> parseTechnique(String[] parts) {
        Map<String, String> tech = new HashMap<>();
        if (parts.length >= 2) tech.put("name", parts[1]);
        if (parts.length >= 3) tech.put("type", parts[2]);
        if (parts.length >= 4) tech.put("owner", parts[3]);
        if (parts.length >= 5) tech.put("damage", parts[4]);
        return tech;
    }

    private Map<String, String> parseTimelineEvent(String[] parts) {
        Map<String, String> event = new HashMap<>();
        if (parts.length >= 2) event.put("timestamp", parts[1]);
        if (parts.length >= 3) event.put("type", parts[2]);
        if (parts.length >= 4) event.put("description", parts[3]);
        return event;
    }

    private Map<String, String> parseEnemyAction(String[] parts) {
        Map<String, String> action = new HashMap<>();
        if (parts.length >= 2) action.put("type", parts[1]);
        if (parts.length >= 3) action.put("name", parts[2]);
        return action;
    }

    private void parseCivilianImpact(Map<String, String> impact, String[] parts) {
        for (int i = 1; i < parts.length; i++) {
            String[] kv = parts[i].split("=", 2);
            if (kv.length == 2) {
                impact.put(kv[0].trim(), kv[1].trim());
            }
        }
    }

    private void parseMissionResult(Map<String, Object> result, String[] parts) {
        if (parts.length >= 2) result.put("outcome", parts[1]);
        if (parts.length >= 3) {
            String damageStr = parts[2].replace("damageCost=", "");
            result.put("damageCost", damageStr);
        }
    }
}