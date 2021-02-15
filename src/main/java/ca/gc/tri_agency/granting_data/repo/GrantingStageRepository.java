package ca.gc.tri_agency.granting_data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.tri_agency.granting_data.model.GrantingStage;
import ca.gc.tri_agency.granting_data.model.projection.GrantingStageProjection;

@Repository
@Transactional(readOnly = true)
public interface GrantingStageRepository extends JpaRepository<GrantingStage, Long> { // @formatter:off

	@Query("SELECT id AS id, nameEn AS nameEn, nameFr AS nameFr"
			+ " FROM GrantingStage"
			+ " ORDER BY nameEn")
	List<GrantingStageProjection> findAllNames();

} // @formatter:on
