package parser;

import exception.MissionParsingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class IniFormatParser extends MissionParser {

    @Override
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try {
            String content = Files.readString(file);
            return parseIniToMap(content);
        } catch (IOException e) {
            throw new MissionParsingException("Ошибка чтения INI файла: " + file, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseIniToMap(String content) {
        Map<String, Object> result = new HashMap<>();
        Map<String, List<Map<String, String>>> collections = new HashMap<>();

        String currentSection = null;
        Map<String, String> currentData = new HashMap<>();

        for (String line : content.lines().toList()) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }

            // Секция [NAME]
            if (line.startsWith("[") && line.endsWith("]")) {
                // Сохраняем предыдущую секцию
                if (currentSection != null && !currentData.isEmpty()) {
                    saveSection(result, collections, currentSection, currentData);
                }
                currentSection = line.substring(1, line.length() - 1).trim().toUpperCase();
                currentData = new HashMap<>();
                continue;
            }

            // Ключ=значение
            if (currentSection != null && line.contains("=")) {
                int eqIdx = line.indexOf('=');
                String key = line.substring(0, eqIdx).trim();
                String value = line.substring(eqIdx + 1).trim();
                currentData.put(key, value);
            }
        }

        // Сохраняем последнюю секцию
        if (currentSection != null && !currentData.isEmpty()) {
            saveSection(result, collections, currentSection, currentData);
        }

        // Добавляем коллекции в результат
        for (Map.Entry<String, List<Map<String, String>>> entry : collections.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        if (result.containsKey("mission")) {
            Map<String, Object> mission = (Map<String, Object>) result.get("mission");

            // Удаляем mission и добавляем его содержимое
            result.remove("mission");
            result.putAll(mission);
        }

        return result;
    }

    private void saveSection(Map<String, Object> result,
                             Map<String, List<Map<String, String>>> collections,
                             String sectionName,
                             Map<String, String> data) {

        // Уникальная секция — сохраняем как есть
        String key = sectionName.toLowerCase();
        if (!result.containsKey(key)) {
            result.put(key, data);
        } else {
            // Если ключ уже есть — превращаем в коллекцию
            List<Map<String, String>> list = new ArrayList<>();
            @SuppressWarnings("unchecked")
            Map<String, String> existing = (Map<String, String>) result.get(key);
            list.add(existing);
            list.add(data);
            result.put(key, list);
        }
    }
}