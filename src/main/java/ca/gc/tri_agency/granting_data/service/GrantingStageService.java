package ca.gc.tri_agency.granting_data.service;

import java.util.List;

import ca.gc.tri_agency.granting_data.model.GrantingStage;
import ca.gc.tri_agency.granting_data.model.projection.GrantingStageProjection;

public interface GrantingStageService {

	GrantingStage findGrantingStageById(Long id);
	
	List<GrantingStage> findAllGrantingStages();

	List<GrantingStageProjection> findAllGrantingStageNames();
}
