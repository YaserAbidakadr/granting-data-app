package ca.gc.tri_agency.granting_data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.tri_agency.granting_data.model.GrantingSystem;
import ca.gc.tri_agency.granting_data.model.projection.GrantingSystemProjection;

@Repository
@Transactional(readOnly = true)
public interface GrantingSystemRepository extends JpaRepository<GrantingSystem, Long> { // @formatter:off

	GrantingSystem findByAcronym(String string);

	@Query("SELECT id AS id, acronym AS acronym"
			+ " FROM GrantingSystem"
			+ " ORDER BY acronym")
	List<GrantingSystemProjection> findAllAcronyms();
	
} // @formatter:on
