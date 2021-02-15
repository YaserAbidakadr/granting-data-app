package ca.gc.tri_agency.granting_data.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;

import ca.gc.tri_agency.granting_data.model.ApplicationParticipation;
import ca.gc.tri_agency.granting_data.model.GrantingSystem;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.model.dto.AppPartEdiAuthorizedDto;
import ca.gc.tri_agency.granting_data.model.projection.ApplicationParticipationProjection;

public interface ApplicationParticipationService {

	List<ApplicationParticipation> generateTestAppParticipations(SystemFundingOpportunity sfo, Instant createDate, long maxApplications,
			long maxParticipants);

	String generateTestAppId(GrantingSystem system);
	// private Long applicationId;
	// amis -> 457315
	// namis -> 453365
	// crm -> 430-2018-00001

	String generateTestApplicationIdentifier(String appId, GrantingSystem system);
	// private String applicationIdentifier;
	// amis -> 611-2018-0092
	// namis -> 535982-2018
	// crm -> 167551C3-6826-4664-8790-AB7A6A8A71F8

	void fillInRandomRole(ApplicationParticipation app, GrantingSystem sys);
	// private String roleCode;
	// amis -> 147
	// namis -> 64
	// crm -> 8878CE1D-6020-E211-AF1A-005056AD024F

	void fillInRandomOrg(ApplicationParticipation app, GrantingSystem sys);

	void fillInRandomPerson(ApplicationParticipation app, GrantingSystem sys);

	long generateTestAppParicipationsForAllSystemFundingOpportunities();

	List<ApplicationParticipation> getAllowedRecords();

	ApplicationParticipation getAllowedRecord(Long id);

	ApplicationParticipation findAppPartByApplId(String applId);

	// private String programId; // from sfo extId
	// amis -> 611
	// namis -> BNCEG
	// crm -> 5BC9640E-4421-E211-AEEA-005056AD550D

	void saveAllApplicationParticipations(List<ApplicationParticipation> appParticipations);

	List<String> getExtIdsQualifiedForEdi();

	List<AppPartEdiAuthorizedDto> findAppPartsForCurrentUserWithEdiAuth();

	ApplicationParticipationProjection findAppPartById(Long apId) throws AccessDeniedException;

	/*
	 * Returns a List even though we are querying for one AP because an applicant can have multiple indigenous identities
	 * and/or can have multiple ethnicities  
	 */
	List<ApplicationParticipationProjection> findAppPartWithEdiData(Long apId) throws AccessDeniedException;
	
	Long[] findAppPartEdiDataForBu(Long buId);

}
