package ca.gc.tri_agency.granting_data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.model.GrantingSystem;
import ca.gc.tri_agency.granting_data.model.projection.GrantingSystemProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.GrantingSystemRepository;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;

@Service
public class GrantingSystemServiceImpl implements GrantingSystemService {
	
	private static final String ENTITY_TYPE = "GrantingSystem";

	private GrantingSystemRepository gsRepo;
	
	private GrantingCapabilityService gcService;

	@Autowired
	public GrantingSystemServiceImpl(GrantingSystemRepository gsRepo, GrantingCapabilityService gcService) {
		this.gsRepo = gsRepo;
		this.gcService = gcService;
	}

	@Override
	public GrantingSystem findGrantingSystemById(Long id) {
		return gsRepo.findById(id).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, id)));
	}

	@Override
	public List<GrantingSystem> findAllGrantingSystems() {
		return gsRepo.findAll();
	}

	@Override
	public GrantingSystem findGrantingSystemByAcronym(String acronym) {
		return gsRepo.findByAcronym(acronym);
	}

	@AdminOnly
	@Override
	public GrantingSystem findGrantingSystemFromFile(String fileName) {
		List<GrantingSystem> grantingSystems = findAllGrantingSystems();
		GrantingSystem gs = null;
		for (GrantingSystem system : grantingSystems) {
			if (fileName.contains(system.getAcronym())) {
				gs = system;
			}
		}
		return gs;
	}

	@Override
	public Map<Long, GrantingSystem> findApplySystemsByFundingOpportunityMap() {
		Map<Long, GrantingSystem> retval = new HashMap<>();
		List<GrantingCapability> applyCapabilities = gcService.findGrantingCapabilitiesByGrantingStageNameEn("APPLY");
		for (GrantingCapability c : applyCapabilities) {
			retval.put(c.getFundingOpportunity().getId(), c.getGrantingSystem());
		}
		return retval;
	}

	@Override
	public Map<Long, List<GrantingSystem>> findAwardSystemsByFundingOpportunityMap() {
		Map<Long, List<GrantingSystem>> retval = new HashMap<Long, List<GrantingSystem>>();
		List<GrantingCapability> applyCapabilities = gcService.findGrantingCapabilitiesByGrantingStageNameEn("AWARD");
		for (GrantingCapability c : applyCapabilities) {
			if (retval.containsKey(c.getFundingOpportunity().getId()) == false) {
				retval.put(c.getFundingOpportunity().getId(), new ArrayList<GrantingSystem>());
			}
			retval.get(c.getFundingOpportunity().getId()).add(c.getGrantingSystem());
		}
		return retval;
	}

	@Override
	public List<GrantingSystemProjection> findAllGrantingSystemAcronyms() {
		return gsRepo.findAllAcronyms();
	}

}
