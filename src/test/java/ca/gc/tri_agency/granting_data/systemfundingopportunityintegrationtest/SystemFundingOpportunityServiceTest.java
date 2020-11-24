package ca.gc.tri_agency.granting_data.systemfundingopportunityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
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
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.model.projection.SystemFundingOpportunityProjection;
import ca.gc.tri_agency.granting_data.repo.SystemFundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;
import ca.gc.tri_agency.granting_data.service.SystemFundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class SystemFundingOpportunityServiceTest {

	@Autowired
	private SystemFundingOpportunityService sfoService;

	@Autowired
	private GrantingSystemService gSystemService;

	@Autowired
	private SystemFundingOpportunityRepository sfoRepo;

	@Tag("user_story_14593")
	@WithAnonymousUser
	@Test
	public void test_findSystemFundingOpportunityById_shouldSucceed() {
		assertNotNull(sfoService.findSystemFundingOpportunityById(1L));
	}

	@Tag("user_story_14593")
	@WithAnonymousUser
	@Test
	public void test_findSystemFundingOpportunityById_shouldThrowDataRetrievalFailureException() {
		assertThrows(DataRetrievalFailureException.class, () -> sfoService.findSystemFundingOpportunityById(Long.MAX_VALUE));
	}

	@Tag("user_story_14592")
	@WithAnonymousUser
	@Test
	public void test_findAllSystemFundingOpportunities() {
		assertTrue(0 < sfoService.findAllSystemFundingOpportunities().size());
	}

	@Tag("user_story_14591")
	@WithAnonymousUser
	@Test
	public void test_findSystemFundingOpportunitiesByLinkedFOid() {
		assertTrue(0 < sfoService.findSystemFundingOpportunitiesByLinkedFOid(1L).size());
	}

	@WithAnonymousUser
	@Test
	public void test_findSystemFundingOpportunitiesByExtId() {
		assertTrue(0 < sfoService.findSystemFundingOpportunitiesByExtId("CTAC").size());
	}

	@WithAnonymousUser
	@Test
	public void test_findSystemFundingOpportunitiesByNameEn() {
		assertTrue(0 < sfoService.findSystemFundingOpportunitiesByNameEn("Technology Access Centre").size());
	}

	@Tag("user_story_14591")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_linkSystemFundingOpportunity() {
		SystemFundingOpportunity sfo = new SystemFundingOpportunity();
		sfo.setExtId(RandomStringUtils.randomAlphabetic(10));
		sfo.setNameEn(RandomStringUtils.randomAlphabetic(25));
		sfo.setNameFr(RandomStringUtils.randomAlphabetic(25));
		sfo.setGrantingSystem(gSystemService.findGrantingSystemById(1L));

		long sfoId = sfoService.saveSystemFundingOpportunity(sfo).getId();

		assertNull(sfoService.findSystemFundingOpportunityById(sfoId).getLinkedFundingOpportunity());

		sfoService.linkSystemFundingOpportunity(sfoId, 1L);

		assertEquals(1L, (long) sfoService.findSystemFundingOpportunityById(sfoId).getLinkedFundingOpportunity().getId());
	}

	@Tag("user_story_14659")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_unlinkSystemFundingOpportunity_shouldThrowDataRetrievalFailureException() {
		assertThrows(DataRetrievalFailureException.class, () -> sfoService.unlinkSystemFundingOpportunity(1L, 100L));
	}

	@Tag("user_story_19301")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindAllSystemFundingOpportunityRevisions() {
		List<String[]> revisionList = sfoService.findAllSystemFundingOpportunityRevisions();

		int numAdds = 0;

		for (String[] strArr : revisionList) {
			if (strArr[2].equals("ADD")) {
				++numAdds;
			}
		}

		assertTrue(numAdds >= 10);
	}

	@Tag("user_story_19301")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotFindAllSystemFundingOpportunityRevisions_shouldThrowsException() {
		assertThrows(AccessDeniedException.class, () -> sfoService.findAllSystemFundingOpportunityRevisions());
	}

	@Tag("user_story_19301")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindSystemFundingOpportunityRevisionById() {
		List<String[]> revisionList = sfoService.findSystemFundingOpportunityRevisionById(1L);

		int numAdds = 0;

		for (String[] strArr : revisionList) {
			if (strArr[2].equals("ADD")) {
				++numAdds;
			}
		}

		assertEquals(1, numAdds);
	}

	@Tag("user_story_19301")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotFindSystemFundingOpportunityRevisionById_shouldThrowException() {
		assertThrows(AccessDeniedException.class, () -> sfoService.findSystemFundingOpportunityRevisionById(1L));
	}

	@Tag("user_story_19301")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_auditLogTracksChangesToSFOs() {
		int numRevisions = sfoService.findAllSystemFundingOpportunityRevisions().size();

		SystemFundingOpportunity sfo = sfoService.findSystemFundingOpportunityById(5L);
		sfo.setNameEn(RandomStringUtils.random(25));
		
		sfoService.saveSystemFundingOpportunity(sfo);

		assertEquals(numRevisions + 1, sfoService.findAllSystemFundingOpportunityRevisions().size());
	}

	@Tag("user_story_14591")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_findSystemFundingOpportunityAndFOName() {
		SystemFundingOpportunityProjection sfoProjection = sfoService.findSystemFundingOpportunityAndLinkedFOName(10L);

		assertEquals("Belmont Forum (BFBIO) (5808)", sfoProjection.getFundingOpportunityEn());

		assertThrows(DataRetrievalFailureException.class, () -> sfoService.findSystemFundingOpportunityAndLinkedFOName(Long.MAX_VALUE));
	}

	@Tag("user_story_14592")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_findSystemFundingOpportunityAndLinkedFONameAndGSysName() {
		long initSFOCount = sfoRepo.count();

		SystemFundingOpportunity sfo = new SystemFundingOpportunity();
		sfo.setGrantingSystem(gSystemService.findGrantingSystemById(1L));

		sfoService.saveSystemFundingOpportunity(sfo);

		List<SystemFundingOpportunityProjection> sfoProjectionList = sfoService
				.findAllSystemFundingOpportunitiesAndLinkedFONameAndGSysName();

		// verifies that the result set will also include any SFOs that don't have a linked FO
		assertEquals(initSFOCount + 1, sfoProjectionList.size());
	}

	@Tag("user_story_14592")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_findSystemFundingOpportunityNameAndLinkedFOName() {
		SystemFundingOpportunityProjection sfoProjection = sfoService.findSystemFundingOpportunityNameAndLinkedFOName(8L);

		assertEquals("SSHRC Postdoctoral Fellowships", sfoProjection.getNameEn());
		assertEquals("IRC", sfoProjection.getFundingOpportunityEn());

		assertThrows(DataRetrievalFailureException.class,
				() -> sfoService.findSystemFundingOpportunityNameAndLinkedFOName(Long.MAX_VALUE));
	}

}
