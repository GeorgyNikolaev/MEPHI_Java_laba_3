package service;

import api.dto.MissionSummaryDto;
import api.dto.ReportResponseDto;
import domain.Mission;
import enums.MissionOutcome;
import enums.ReportType;
import enums.ThreatLevel;
import exception.MissionParsingException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import parser.MissionParser;
import parser.MissionParserFactory;
import persistence.entity.MissionEntity;
import persistence.entity.MissionReportEntity;
import persistence.repository.MissionJpaRepository;
import persistence.repository.MissionReportJpaRepository;
import reporter.MissionReporter;
import reporter.Report;
import reporter.ReportFactory;
import support.MissionTestFixtures;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionApplicationServiceTest {

    @Mock
    private MissionJpaRepository missionRepository;
    @Mock
    private MissionReportJpaRepository reportRepository;
    @Mock
    private MissionParserFactory parserFactory;
    @Mock
    private ReportFactory reportFactory;
    @Mock
    private MissionMapper missionMapper;
    @Mock
    private MissionParser missionParser;

    @InjectMocks
    private MissionApplicationService service;

    @Test
    void uploadMission_emptyFile_rejectsInput() {
        MockMultipartFile empty = new MockMultipartFile("file", "x.json", "application/json", new byte[0]);

        assertThrows(MissionParsingException.class, () -> service.uploadMission(empty));
        verifyNoInteractions(missionRepository);
    }

    @Test
    void uploadMission_newMission_savesEntity() throws Exception {
        Mission mission = MissionTestFixtures.sampleMission();
        MissionEntity entity = MissionTestFixtures.sampleEntity(null);
        MissionEntity saved = MissionTestFixtures.sampleEntity(10L);

        MockMultipartFile file = new MockMultipartFile(
                "file", "mission.json", "application/json", "{}".getBytes());

        when(parserFactory.getParser(any(Path.class))).thenReturn(missionParser);
        when(missionParser.parse(any(Path.class))).thenReturn(mission);
        when(missionRepository.findByMissionCode("M-FIXTURE-01")).thenReturn(Optional.empty());
        when(missionMapper.newEntityFromDomain(mission, "mission.json")).thenReturn(entity);
        when(missionRepository.save(entity)).thenReturn(saved);

        MissionSummaryDto dto = service.uploadMission(file);

        assertEquals(10L, dto.id());
        assertEquals("M-FIXTURE-01", dto.missionCode());
        verify(missionMapper).newEntityFromDomain(mission, "mission.json");
        verify(missionMapper, never()).replaceMissionPayload(any(), any(), any());
    }

    @Test
    void uploadMission_existingMission_updatesInsteadOfInsert() throws Exception {
        Mission mission = MissionTestFixtures.sampleMission();
        MissionEntity existing = MissionTestFixtures.sampleEntity(5L);
        MissionEntity saved = MissionTestFixtures.sampleEntity(5L);

        MockMultipartFile file = new MockMultipartFile(
                "file", "mission.json", "application/json", "{}".getBytes());

        when(parserFactory.getParser(any(Path.class))).thenReturn(missionParser);
        when(missionParser.parse(any(Path.class))).thenReturn(mission);
        when(missionRepository.findByMissionCode("M-FIXTURE-01")).thenReturn(Optional.of(existing));
        when(missionRepository.save(existing)).thenReturn(saved);

        MissionSummaryDto dto = service.uploadMission(file);

        assertEquals(5L, dto.id());
        verify(missionMapper).replaceMissionPayload(existing, mission, "mission.json");
        verify(missionMapper, never()).newEntityFromDomain(any(), any());
    }

    @Test
    void deleteMission_unknownId_throwsNotFound() {
        when(missionRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.deleteMission(99L));
        verify(missionRepository, never()).deleteById(any());
    }

    @Test
    void deleteMission_existing_deletes() {
        when(missionRepository.existsById(1L)).thenReturn(true);

        service.deleteMission(1L);

        verify(missionRepository).deleteById(1L);
    }

    @Test
    void createReport_unknownMission_throwsNotFound() {
        when(missionRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.createReport(7L, ReportType.SUMMARY));
    }

    @Test
    void createReport_validMission_returnsReportAndPersists() {
        MissionEntity entity = MissionTestFixtures.sampleEntity(3L);
        Mission domain = MissionTestFixtures.sampleMission();
        MissionReporter reporter = mock(MissionReporter.class);
        Report generated = new Report("REPORT BODY", ReportType.DETAILED);

        when(missionRepository.findById(3L)).thenReturn(Optional.of(entity));
        when(missionMapper.toDomain(entity)).thenReturn(domain);
        when(reportFactory.createReporter(ReportType.DETAILED)).thenReturn(reporter);
        when(reporter.generate(domain)).thenReturn(generated);
        when(reportRepository.save(any(MissionReportEntity.class))).thenAnswer(inv -> {
            MissionReportEntity r = inv.getArgument(0);
            r.setId(100L);
            r.setCreatedAt(Instant.parse("2024-10-12T12:00:00Z"));
            return r;
        });

        ReportResponseDto dto = service.createReport(3L, ReportType.DETAILED);

        assertEquals(100L, dto.id());
        assertEquals(3L, dto.missionId());
        assertEquals(ReportType.DETAILED, dto.reportType());
        assertEquals("REPORT BODY", dto.content());

        ArgumentCaptor<MissionReportEntity> captor = ArgumentCaptor.forClass(MissionReportEntity.class);
        verify(reportRepository).save(captor.capture());
        assertEquals(entity, captor.getValue().getMission());
        assertEquals("REPORT BODY", captor.getValue().getContent());
    }

    @Test
    void listReports_unknownMission_throwsNotFound() {
        when(missionRepository.existsById(8L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.listReports(8L));
    }

    @Test
    void listReports_existing_returnsMappedDtos() {
        MissionEntity entity = MissionTestFixtures.sampleEntity(2L);
        MissionReportEntity report = new MissionReportEntity();
        report.setId(11L);
        report.setMission(entity);
        report.setReportType(ReportType.SUMMARY);
        report.setContent("text");
        report.setCreatedAt(Instant.parse("2024-10-12T11:00:00Z"));

        when(missionRepository.existsById(2L)).thenReturn(true);
        when(reportRepository.findByMission_IdOrderByCreatedAtDesc(2L)).thenReturn(List.of(report));

        List<ReportResponseDto> list = service.listReports(2L);

        assertEquals(1, list.size());
        assertEquals(11L, list.get(0).id());
        assertEquals(ReportType.SUMMARY, list.get(0).reportType());
    }
}
