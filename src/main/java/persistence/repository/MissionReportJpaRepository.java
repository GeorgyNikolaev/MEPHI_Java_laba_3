package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import persistence.entity.MissionReportEntity;

import java.util.List;

public interface MissionReportJpaRepository extends JpaRepository<MissionReportEntity, Long> {

    List<MissionReportEntity> findByMission_IdOrderByCreatedAtDesc(Long missionId);
}
