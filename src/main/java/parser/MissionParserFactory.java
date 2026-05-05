package parser;

import exception.MissionParsingException;

import java.nio.file.Path;

public class MissionParserFactory {

    public MissionParser getParser(Path file) throws MissionParsingException {
        String fileName = file.getFileName().toString().toLowerCase();
        String ext = getExtension(fileName);

        return switch (ext) {
            case "json" -> new JsonMissionParser();
            case "xml" -> new XmlMissionParser();
            case "yaml", "yml" -> new YamlMissionParser();
            case "txt" -> new TextMissionParser();
            case "" -> {
                // Файл без расширения — пробуем определить по содержимому
                yield detectParserByContent(file);
            }
            default -> throw new MissionParsingException(
                    "Неподдерживаемое расширение файла: " + ext
            );
        };
    }

    /**
     * Определяет парсер по содержимому файла (для файлов без расширения)
     */
    private MissionParser detectParserByContent(Path file) throws MissionParsingException {
        try {
            String content = java.nio.file.Files.readString(file).trim();

            if (content.startsWith("{") || content.startsWith("[")) {
                return new JsonMissionParser();
            } else if (content.startsWith("<")) {
                return new XmlMissionParser();
            } else if (content.contains(":") && !content.contains("=") && !content.contains("|")) {
                return new YamlMissionParser();
            } else if (content.contains("[MISSION]") || content.contains("[CURSE]")) {
                return new TextMissionParser(); // INI
            } else if (content.contains("|") && content.contains("MISSION_CREATED")) {
                return new TextMissionParser(); // Delimited
            } else if (content.contains(".name:") || content.contains("[0].")) {
                return new TextMissionParser(); // Properties
            }

            // По умолчанию пробуем INI-парсер
            return new DelimitedLogParser();

        } catch (Exception e) {
            throw new MissionParsingException("Не удалось определить формат файла: " + file, e);
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) return "";
        return filename.substring(lastDot + 1);
    }
}