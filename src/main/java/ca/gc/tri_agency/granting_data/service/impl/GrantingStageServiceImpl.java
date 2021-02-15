package ca.gc.tri_agency.granting_data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import ca.gc.tri_agency.granting_data.model.GrantingStage;
import ca.gc.tri_agency.granting_data.model.projection.GrantingStageProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.GrantingStageRepository;
import ca.gc.tri_agency.granting_data.service.GrantingStageService;

@Service
public class GrantingStageServiceImpl implements GrantingStageService {

	private static final String ENTITY_TYPE = "GrantingStage";

	private GrantingStageRepository gStageRepo;

	@Autowired
	public GrantingStageServiceImpl(GrantingStageRepository gStageRepo) {
		this.gStageRepo = gStageRepo;
	}

	@Override
	public GrantingStage findGrantingStageById(Long id) {
		return gStageRepo.findById(id).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, id)));
	}

	@Override
	public List<GrantingStage> findAllGrantingStages() {
		return gStageRepo.findAll();
	}

	@Override
	public List<GrantingStageProjection> findAllGrantingStageNames() {
		return gStageRepo.findAllNames();
	}

}
