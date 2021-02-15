package ca.gc.tri_agency.granting_data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.model.projection.GrantingCapabilityProjection;

@Repository
@Transactional(readOnly = true)
public interface GrantingCapabilityRepository extends JpaRepository<GrantingCapability, Long> { // @formatter:off

	List<GrantingCapability> findByFundingOpportunityId(Long id);

	List<GrantingCapability> findByGrantingStageNameEn(String name);
	
	@Query("SELECT gc.id AS id, gc.description AS description, gc.url AS url, gStg.nameEn AS grantingStageNameEn,"
			+ " gStg.nameFr AS grantingStageNameFr, gSys.nameEn AS grantingSystemNameEn, gSys.nameFr AS grantingSystemNameFr"
			+ " FROM GrantingCapability gc"
			+ " JOIN GrantingStage gStg ON gc.grantingStage.id = gStg.id"
			+ " JOIN GrantingSystem gSys ON gc.grantingSystem.id = gSys.id"
			+ " WHERE gc.fundingOpportunity.id = ?1")
	List<GrantingCapabilityProjection> findForBrowseViewFO(Long foId);

	@Query("SELECT gc, fo, gstg, gsys"
			+ " FROM GrantingCapability gc"
			+ " LEFT JOIN FETCH FundingOpportunity fo ON gc.fundingOpportunity.id = fo.id"
			+ " LEFT JOIN FETCH GrantingStage gstg ON gc.grantingStage.id = gstg.id"
			+ " LEFT JOIN FETCH GrantingSystem gsys ON gc.grantingSystem.id = gsys.id"
			+ " WHERE gc.id = ?1")
	GrantingCapability findByIdEager(Long gcId);

} // @formatter:on
