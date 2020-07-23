package ca.gc.tri_agency.granting_data.systemfundingopportunityintegrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;
import ca.gc.tri_agency.granting_data.service.SystemFundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SystemFundingOpportunityServiceTest {

	@Autowired
	private SystemFundingOpportunityService sfoService;

	@Autowired
	private GrantingSystemService gSystemService;

	@WithAnonymousUser
	@Test
	public void test_findSystemFundingOpportunityById_shouldSucceed() {
		assertNotNull(sfoService.findSystemFundingOpportunityById(1L));
	}

	@WithAnonymousUser
	@Test(expected = DataRetrievalFailureException.class)
	public void test_findSystemFundingOpportunityById_shouldThrowDataRetrievalFailureException() {
		sfoService.findSystemFundingOpportunityById(Long.MAX_VALUE);
	}

	@WithAnonymousUser
	@Test
	public void test_findAllSystemFundingOpportunities() {
		assertTrue(0 < sfoService.findAllSystemFundingOpportunities().size());
	}

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

	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test(expected = DataRetrievalFailureException.class)
	public void test_unlinkSystemFundingOpportunity_shouldThrowDataRetrievalFailureException() {
		sfoService.unlinkSystemFundingOpportunity(1L, 100L);
	}

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

	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test(expected = AccessDeniedException.class)
	public void test_nonAdminCannotFindAllSystemFundingOpportunityRevisionsShouldThrowsException() {
		sfoService.findAllSystemFundingOpportunityRevisions();
	}

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

	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test(expected = AccessDeniedException.class)
	public void test_nonAdminCannotFindSystemFundingOpportunityRevisionByIdShouldThrowException() {
		sfoService.findSystemFundingOpportunityRevisionById(1L);
	}
}
