package parser;

import domain.*;
import enums.MissionOutcome;
import enums.SorcererRank;
import enums.TechniqueType;
import enums.ThreatLevel;
import exception.MissionParsingException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesFormatParser extends MissionParser {

    private static final Pattern INDEXED_KEY = Pattern.compile("^(\\w+)\\[(\\d+)\\]\\.(.+)$");

    @Override
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try {
            String content = Files.readString(file);
            return parsePropertiesToMap(content);
        } catch (IOException e) {
            throw new MissionParsingException("Ошибка чтения Properties файла: " + file, e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parsePropertiesToMap(String content) {
        Map<String, Object> result = new HashMap<>();
        Properties props = new Properties();

        try {
            // Заменяем : на = для совместимости
            String normalized = content.replace(":", "=");
            props.load(new StringReader(normalized));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка парсинга properties", e);
        }

        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            setNestedValue(result, key, value);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void setNestedValue(Map<String, Object> root, String fullKey, String value) {
        Matcher matcher = INDEXED_KEY.matcher(fullKey);

        if (matcher.matches()) {
            // Ключ с индексом: sorcerer[0].name
            String collectionName = matcher.group(1);
            int index = Integer.parseInt(matcher.group(2));
            String innerKey = matcher.group(3);

            List<Map<String, String>> list = (List<Map<String, String>>)
                    root.computeIfAbsent(collectionName, k -> new ArrayList<>());

            // Расширяем список до нужного индекса
            while (list.size() <= index) {
                list.add(new HashMap<>());
            }

            list.get(index).put(innerKey, value);

        } else if (fullKey.contains(".")) {
            // Вложенный ключ: curse.name
            String[] parts = fullKey.split("\\.", 2);
            String outerKey = parts[0];
            String innerKey = parts[1];

            Map<String, Object> outer = (Map<String, Object>)
                    root.computeIfAbsent(outerKey, k -> new HashMap<>());

            if (outer instanceof Map) {
                ((Map<String, Object>) outer).put(innerKey, value);
            }
        } else {
            // Простой ключ: missionId
            root.put(fullKey, value);
        }
    }
}
