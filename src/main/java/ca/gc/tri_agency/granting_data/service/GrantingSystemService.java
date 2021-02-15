package ca.gc.tri_agency.granting_data.service;

import java.util.List;
import java.util.Map;

import ca.gc.tri_agency.granting_data.model.GrantingSystem;
import ca.gc.tri_agency.granting_data.model.projection.GrantingSystemProjection;

public interface GrantingSystemService {

	GrantingSystem findGrantingSystemById(Long id);
	
	List<GrantingSystem> findAllGrantingSystems();
	
	GrantingSystem findGrantingSystemByAcronym(String acronym);
	
	GrantingSystem findGrantingSystemFromFile(String fileName);
	
	Map<Long, GrantingSystem> findApplySystemsByFundingOpportunityMap();
	
	Map<Long, List<GrantingSystem>> findAwardSystemsByFundingOpportunityMap();
	
	List<GrantingSystemProjection> findAllGrantingSystemAcronyms();
	
}
