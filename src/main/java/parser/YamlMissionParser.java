package parser;

import exception.MissionParsingException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class YamlMissionParser extends MissionParser {

    private final Yaml yaml;

    public YamlMissionParser() {
        LoaderOptions options = new LoaderOptions();
        this.yaml = new Yaml(new SafeConstructor(options));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try (InputStream input = Files.newInputStream(file)) {
            Object parsed = yaml.load(input);
            if (parsed instanceof Map) {
                return (Map<String, Object>) parsed;
            }
            throw new MissionParsingException("YAML файл должен содержать объект на верхнем уровне");
        } catch (IOException e) {
            throw new MissionParsingException("Ошибка чтения YAML файла: " + file, e);
        } catch (Exception e) {
            throw new MissionParsingException("Ошибка парсинга YAML: " + e.getMessage(), e);
        }
    }
}