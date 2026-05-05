package parser;

import domain.Mission;
import domain.Sorcerer;
import enums.MissionOutcome;
import enums.SorcererRank;
import enums.TechniqueType;
import enums.ThreatLevel;
import exception.MissionParsingException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class MissionParser {

    public abstract Map<String, Object> parseToMap(Path file) throws MissionParsingException;

    public Mission parse(Path file) throws MissionParsingException {
        Map<String, Object> rawData = parseToMap(file);
        return MissionConverter.convertToMission(rawData);
    }

    protected long parseLongSafe(String raw) {
        return parseLongSafe(raw, 0L);
    }

    protected long parseLongSafe(String raw, long defaultValue) {
        if (raw == null) return defaultValue;
        try {
            return Long.parseLong(raw.trim());
        } catch (Exception e) {
            System.out.println("Возникла ошибка при прасинге числа: " + e.getMessage());
            return defaultValue;
        }
    }

    protected MissionOutcome parseOutcome(String raw) {
        if (raw == null) return null;
        try { return MissionOutcome.valueOf(raw.trim()); }
        catch (Exception e) { return null; }
    }

    protected ThreatLevel parseThreatLevel(String raw) {
        if (raw == null) return null;
        try { return ThreatLevel.valueOf(raw.trim()); }
        catch (Exception e) { return null; }
    }

    protected SorcererRank parseSorcererRank(String raw) {
        if (raw == null) return null;
        try { return SorcererRank.valueOf(raw.trim()); }
        catch (Exception e) { return null; }
    }

    protected TechniqueType parseTechniqueType(String raw) {
        if (raw == null) return null;
        try { return TechniqueType.valueOf(raw.trim()); }
        catch (Exception e) { return null; }
    }

    protected Sorcerer findSorcererByName(List<Sorcerer> list, String name) {
        if (name == null) return null;
        for (Sorcerer s : list) {
            if (s.getName() != null && s.getName().equals(name)) return s;
        }
        return null;
    }
}