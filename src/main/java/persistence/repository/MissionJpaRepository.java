package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import persistence.entity.MissionEntity;

import java.util.List;
import java.util.Optional;

public interface MissionJpaRepository extends JpaRepository<MissionEntity, Long> {

    boolean existsByMissionCode(String missionCode);

    Optional<MissionEntity> findByMissionCode(String missionCode);

    List<MissionEntity> findAllByOrderByCreatedAtDesc();
}
