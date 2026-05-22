package service;

import api.dto.*;
import domain.Mission;
import enums.ReportType;
import exception.MissionParsingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import parser.MissionParser;
import parser.MissionParserFactory;
import persistence.entity.MissionEntity;
import persistence.entity.MissionReportEntity;
import persistence.entity.SorcererEntity;
import persistence.entity.TechniqueUsageEntity;
import persistence.repository.MissionJpaRepository;
import persistence.repository.MissionReportJpaRepository;
import reporter.MissionReporter;
import reporter.Report;
import reporter.ReportFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MissionApplicationService {

    private final MissionJpaRepository missionRepository;
    private final MissionReportJpaRepository reportRepository;
    private final MissionParserFactory parserFactory;
    private final ReportFactory reportFactory;
    private final MissionMapper missionMapper;

    public MissionApplicationService(
            MissionJpaRepository missionRepository,
            MissionReportJpaRepository reportRepository,
            MissionParserFactory parserFactory,
            ReportFactory reportFactory,
            MissionMapper missionMapper) {
        this.missionRepository = missionRepository;
        this.reportRepository = reportRepository;
        this.parserFactory = parserFactory;
        this.reportFactory = reportFactory;
        this.missionMapper = missionMapper;
    }

    @Transactional
    public MissionSummaryDto uploadMission(MultipartFile file) throws MissionParsingException, IOException {
        if (file == null || file.isEmpty()) {
            throw new MissionParsingException("Файл пустой или не передан");
        }
        String original = file.getOriginalFilename();
        String suffix = suffixFromOriginalName(original);

        Path temp = Files.createTempFile("mission-upload-", suffix);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            MissionParser parser = parserFactory.getParser(temp);
            Mission mission = parser.parse(temp);
            String filename = original != null ? original : "upload";
            if (mission.getMissionId() != null) {
                Optional<MissionEntity> existing = missionRepository.findByMissionCode(mission.getMissionId());
                if (existing.isPresent()) {
                    MissionEntity row = existing.get();
                    missionMapper.replaceMissionPayload(row, mission, filename);
                    return toSummary(missionRepository.save(row));
                }
            }
            MissionEntity entity = missionMapper.newEntityFromDomain(mission, filename);
            MissionEntity saved = missionRepository.save(entity);
            return toSummary(saved);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    @Transactional
    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new EntityNotFoundException("Mission not found: " + id);
        }
        missionRepository.deleteById(id);
    }

    @Transactional
    public MissionDetailDto getMissionDetail(Long id) {
        MissionEntity e = missionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found: " + id));
        return toDetail(e);
    }

    @Transactional
    public List<MissionSummaryDto> listMissions() {
        return missionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional
    public ReportResponseDto createReport(Long missionId, ReportType reportType) {
        MissionEntity entity = missionRepository.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("Mission not found: " + missionId));

        Mission mission = missionMapper.toDomain(entity);
        MissionReporter reporter = reportFactory.createReporter(reportType);
        Report report = reporter.generate(mission);

        MissionReportEntity savedReport = new MissionReportEntity();
        savedReport.setMission(entity);
        savedReport.setReportType(report.getType());
        savedReport.setContent(report.getContent());
        reportRepository.save(savedReport);

        return new ReportResponseDto(
                savedReport.getId(),
                entity.getId(),
                savedReport.getReportType(),
                savedReport.getContent(),
                savedReport.getCreatedAt());
    }

    @Transactional
    public List<ReportResponseDto> listReports(Long missionId) {
        if (!missionRepository.existsById(missionId)) {
            throw new EntityNotFoundException("Mission not found: " + missionId);
        }
        return reportRepository.findByMission_IdOrderByCreatedAtDesc(missionId).stream()
                .map(r -> new ReportResponseDto(
                        r.getId(),
                        missionId,
                        r.getReportType(),
                        r.getContent(),
                        r.getCreatedAt()))
                .toList();
    }

    private MissionSummaryDto toSummary(MissionEntity e) {
        return new MissionSummaryDto(
                e.getId(),
                e.getMissionCode(),
                e.getOperationDate(),
                e.getLocation(),
                e.getOutcome(),
                e.getCreatedAt(),
                e.getSourceFilename());
    }

    private MissionDetailDto toDetail(MissionEntity e) {
        CurseDto curse = null;
        if (e.getCurse() != null) {
            curse = new CurseDto(
                    e.getCurse().getName(),
                    e.getCurse().getThreatLevel().name());
        }
        List<SorcererDto> sorcerers = e.getSorcerers().stream()
                .sorted(Comparator.comparingInt(SorcererEntity::getPos))
                .map(s -> new SorcererDto(s.getName(), s.getRank().name()))
                .toList();
        List<TechniqueUsageDto> techniques = e.getTechniques().stream()
                .sorted(Comparator.comparingInt(TechniqueUsageEntity::getPos))
                .map(t -> new TechniqueUsageDto(
                        t.getTechniqueName(),
                        t.getTechniqueType().name(),
                        t.getOwner() != null ? t.getOwner().getName() : null,
                        t.getDamage()))
                .toList();
        return new MissionDetailDto(
                e.getId(),
                e.getMissionCode(),
                e.getOperationDate(),
                e.getLocation(),
                e.getOutcome(),
                e.getDamageCost(),
                e.getComment(),
                e.getSourceFilename(),
                e.getCreatedAt(),
                curse,
                sorcerers,
                techniques,
                List.copyOf(e.getOperationTags()),
                List.copyOf(e.getNotes()));
    }

    private static String suffixFromOriginalName(String original) {
        if (original == null || !original.contains(".")) {
            return ".tmp";
        }
        String ext = original.substring(original.lastIndexOf('.'));
        return ext.length() > 16 ? ".tmp" : ext;
    }
}
