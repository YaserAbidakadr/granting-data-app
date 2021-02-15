package ca.gc.tri_agency.granting_data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.model.projection.GrantingCapabilityProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.GrantingCapabilityRepository;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;

@Service
public class GrantingCapabilityServiceImpl implements GrantingCapabilityService {

	private GrantingCapabilityRepository gcRepo;
	
	private static final String ENTITY_TYPE = "GrantingCapability";

	@Autowired
	public GrantingCapabilityServiceImpl(GrantingCapabilityRepository gcRepo) {
		this.gcRepo = gcRepo;
	}

	@Override
	public GrantingCapability findGrantingCapabilityById(Long id) {
		return gcRepo.findById(id).orElseThrow(
				() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, id)));
	}

	@Override
	public List<GrantingCapability> findAllGrantingCapabilities() {
		return gcRepo.findAll();
	}

	@Override
	@AdminOnly
	public GrantingCapability saveGrantingCapability(GrantingCapability gc) {
		return gcRepo.save(gc);
	}

	@Override
	public List<GrantingCapability> findGrantingCapabilitiesByFoId(Long id) {
		return gcRepo.findByFundingOpportunityId(id);
	}

	@Override
	@AdminOnly
	@Transactional
	public void deleteGrantingCapabilityById(Long id) {
		gcRepo.delete(findGrantingCapabilityById(id));
	}

	@Override
	public List<GrantingCapability> findGrantingCapabilitiesByGrantingStageNameEn(String nameEn) {
		return gcRepo.findByGrantingStageNameEn(nameEn);
	}

	@Override
	public List<GrantingCapabilityProjection> findGrantingCapabilitiesForBrowseViewFO(Long foId) {
		return gcRepo.findForBrowseViewFO(foId);
	}

	@Override
	public GrantingCapability findGrantingCapabilityEager(Long gcId) {
		GrantingCapability gc = gcRepo.findByIdEager(gcId);
		
		if (gc == null) {
			throw new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, gcId));
		}
		
		return gc;
	}

}
