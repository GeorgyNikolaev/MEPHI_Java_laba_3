package parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.MissionParsingException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class JsonMissionParser extends MissionParser {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try {
            // Читаем весь JSON в универсальную структуру
            return mapper.readValue(file.toFile(), new TypeReference<Map<String, Object>>() {});

        } catch (IOException e) {
            throw new MissionParsingException("Ошибка чтения JSON файла: " + file, e);
        } catch (RuntimeException e) {
            throw new MissionParsingException("Недопустимая структура JSON: " + e.getMessage(), e);
        }
    }
}