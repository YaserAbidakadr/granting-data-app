package ca.gc.tri_agency.granting_data.grantingcapabilityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.repo.GrantingCapabilityRepository;
import ca.gc.tri_agency.granting_data.repo.GrantingStageRepository;
import ca.gc.tri_agency.granting_data.repo.GrantingSystemRepository;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class GrantingCapabilityServiceTest {

	@Autowired
	private FundingOpportunityRepository foRepo;
	
	@Autowired
	private GrantingStageRepository gStageRepo;
	
	@Autowired
	private GrantingSystemRepository gSysRepo;
	
	@Autowired
	private GrantingCapabilityService gcService;

	@Autowired
	private GrantingCapabilityRepository gcRepo;

	@Tag("user_story_19005")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanDeleteGC() {
		long numGCs = gcRepo.count();

		gcService.deleteGrantingCapabilityById(100L);

		assertEquals(numGCs - 1, gcRepo.count());

		assertThrows(DataRetrievalFailureException.class, () -> gcService.findGrantingCapabilityById(100L));
	}

	@Tag("user_story_19005")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotDeleteGC_shouldThrowAccessDeniedExcepction() {
		assertThrows(AccessDeniedException.class, () -> gcService.deleteGrantingCapabilityById(101L));
	}

	@Tag("user_story_19004")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanEditGC() {
		long initGcRepoCount = gcRepo.count();

		GrantingCapability gc = gcService.findGrantingCapabilityById(1L);
		String editDescription = "TEST DESCRIPTION EDIT";
		gc.setDescription(editDescription);
		gc.setUrl("https://www.testurl.com/");
		gc.setGrantingStage(gStageRepo.getOne(1L));
		gc.setGrantingSystem(gSysRepo.getOne(1L));
		gc.setFundingOpportunity(foRepo.getOne(1L));
		
		gcService.saveGrantingCapability(gc);

		assertTrue(gcService.findGrantingCapabilityById(1L).getDescription().equals(editDescription));
		assertEquals(initGcRepoCount, gcRepo.count());
	}

	@Tag("user_story_19004")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotEditGC_shouldThrowAccessDeniedException() {
		GrantingCapability gc = gcService.findGrantingCapabilityById(1L);
		String editDescription = "TEST DESCRIPTION EDIT";
		gc.setDescription(editDescription);
		assertThrows(AccessDeniedException.class, () -> gcService.saveGrantingCapability(gc));
	}

	@Tag("user_story_19004")
	@WithAnonymousUser
	@Test
	public void test_findGrantingCapabilityAndFO() {
		GrantingCapability gc = gcService.findGrantingCapabilityEager(155L);

		assertEquals("PromoScience (5390)", gc.getFundingOpportunity().getNameEn());
		assertEquals("AWARD", gc.getGrantingStage().getNameEn());
		assertEquals("NAMIS", gc.getGrantingSystem().getAcronym());
		
		assertThrows(DataRetrievalFailureException.class, () -> gcService.findGrantingCapabilityEager(Long.MAX_VALUE));
	}
	
	@Tag("user_story_14572")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_createGrantingCapabaility() {
		GrantingCapability newGc = new GrantingCapability();
		
		newGc.setDescription("TEST GRANTING CAPABILITY");
		newGc.setUrl("https://www.testGrantingCapability.com");
		newGc.setGrantingStage(gStageRepo.getOne(1L));
		newGc.setGrantingSystem(gSysRepo.getOne(1L));
		newGc.setFundingOpportunity(foRepo.getOne(1L));

		GrantingCapability addedGc = gcService.saveGrantingCapability(newGc);

		assertNotNull(addedGc.getId());
		assertEquals(addedGc.getDescription(), newGc.getDescription());
		assertEquals(1L, addedGc.getFundingOpportunity().getId());
	}

}
