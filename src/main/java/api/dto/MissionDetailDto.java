package api.dto;

import enums.MissionOutcome;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record MissionDetailDto(
        Long id,
        String missionCode,
        LocalDate date,
        String location,
        MissionOutcome outcome,
        Long damageCost,
        String comment,
        String sourceFilename,
        Instant createdAt,
        CurseDto curse,
        List<SorcererDto> sorcerers,
        List<TechniqueUsageDto> techniques,
        List<String> operationTags,
        List<String> notes
) {
}
