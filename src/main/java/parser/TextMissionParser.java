package parser;

import exception.MissionParsingException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class TextMissionParser extends MissionParser {

    private final List<TextFormatHandler> handlers;

    public TextMissionParser() {
        this.handlers = List.of(
                new IniFormatHandler(),
                new PropertiesFormatHandler(),
                new DelimitedLogHandler()
        );
    }

    @Override
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try {
            String content = java.nio.file.Files.readString(file);

            for (TextFormatHandler handler : handlers) {
                if (handler.canHandle(content, file)) {
                    return handler.parse(content, file);
                }
            }

            throw new MissionParsingException(
                    "Не удалось определить формат текстового файла: " + file
            );
        } catch (IOException e) {
            throw new MissionParsingException(
                    "Не удалось прочитать формат текстового файла: " + file
            );
        }
    }


    private interface TextFormatHandler {
        boolean canHandle(String content, Path file);
        Map<String, Object> parse(String content, Path file) throws MissionParsingException;
    }


    private static class IniFormatHandler implements TextFormatHandler {
        public boolean canHandle(String content, Path file) {
            return content.contains("[MISSION]") || content.contains("[CURSE]");
        }

        public Map<String, Object> parse(String content, Path file) throws MissionParsingException {
            return new IniFormatParser().parseToMap(file);
        }
    }


    private static class PropertiesFormatHandler implements TextFormatHandler {
        public boolean canHandle(String content, Path file) {
            return content.contains(".name:") || content.contains("[0].");
        }

        public Map<String, Object> parse(String content, Path file) throws MissionParsingException {
            return new PropertiesFormatParser().parseToMap(file);
        }
    }


    private static class DelimitedLogHandler implements TextFormatHandler {
        public boolean canHandle(String content, Path file) {
            return content.contains("|") && content.contains("MISSION_CREATED");
        }

        public Map<String, Object> parse(String content, Path file) throws MissionParsingException {
            return new DelimitedLogParser().parseToMap(file);
        }
    }
}