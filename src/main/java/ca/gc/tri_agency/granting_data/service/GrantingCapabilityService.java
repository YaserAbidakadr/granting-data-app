package ca.gc.tri_agency.granting_data.service;

import java.util.List;

import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.model.projection.GrantingCapabilityProjection;

public interface GrantingCapabilityService {

	GrantingCapability findGrantingCapabilityById(Long id);

	List<GrantingCapability> findAllGrantingCapabilities();

	GrantingCapability saveGrantingCapability(GrantingCapability gc);

	List<GrantingCapability> findGrantingCapabilitiesByFoId(Long id);

	void deleteGrantingCapabilityById(Long id);

	List<GrantingCapability> findGrantingCapabilitiesByGrantingStageNameEn(String nameEn);

	List<GrantingCapabilityProjection> findGrantingCapabilitiesForBrowseViewFO(Long foId);

	GrantingCapability findGrantingCapabilityEager(Long gcId);
}
