package ca.gc.tri_agency.granting_data.fundingopportunityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.service.AgencyService;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class FundingOpportunityServiceTest {

	@Autowired
	private FundingOpportunityService foService;

	@Autowired
	private AgencyService agencyService;

	@Autowired
	private FundingOpportunityRepository foRepo;

	@Tag("user_story_14594")
	@WithAnonymousUser
	@Test
	public void test_findFundingOpportunitiesByNameEn() {
		assertEquals(0, foService.findFundingOpportunitiesByNameEn("ZZZZZZZZZZ").size());
		assertEquals(1, foService.findFundingOpportunitiesByNameEn("Digging into Data").size());
	}

	@Tag("user_story_14594")
	@WithAnonymousUser
	@Test
	public void test_findFundingOpportunitiesByBusinessUnit() {
		assertEquals(0, foService.findFundingOpportunitiesByBusinessUnitId(Long.MAX_VALUE).size());
		assertTrue(0 < foService.findFundingOpportunitiesByBusinessUnitId(1L).size());
	}

	@Tag("user_story_19279")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindFundingOpportunityRevisionsById() {
		final Long foId = 1L;
		int startNumRevs = foService.findFundingOpportunityRevisionsById(foId).size();

		FundingOpportunity fo = foService.findFundingOpportunityById(foId);
		String newNameEn = RandomStringUtils.randomAlphabetic(15);
		String newNameFr = RandomStringUtils.randomAlphabetic(15);
		fo.setNameEn(newNameEn);
		fo.setNameFr(newNameFr);
		foService.saveFundingOpportunity(fo);

		List<String[]> foRevs = foService.findFundingOpportunityRevisionsById(foId);
		int endNumRevs = foRevs.size();

		assertEquals(startNumRevs + 1, endNumRevs);
		assertEquals("MOD", foRevs.get(endNumRevs - 1)[2]);
		assertEquals(newNameEn, foRevs.get(endNumRevs - 1)[3]);
		assertEquals(newNameFr, foRevs.get(endNumRevs - 1)[4]);

		int numAdds = 0;
		for (String[] arr : foRevs) {
			if (arr[2].equals("ADD")) {
				++numAdds;
			}
		}
		assertEquals(1, numAdds);
	}

	@Tag("user_story_19279")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotFindFundingOpportunityRevisionsById_shouldThrowException() {
		assertThrows(AccessDeniedException.class, () -> foService.findFundingOpportunityRevisionsById(1L));
	}

	@Tag("user_story_19279")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindAllFundingOpportunitiesRevisions() {
		long numFOs = foRepo.count();
		
		long numAdds = 0;

		List<String[]> foRevs = foService.findAllFundingOpportunitiesRevisions();
		for (String[] strArr : foRevs) {
			if (strArr[2].equals("ADD")) {
				++numAdds;
			}
		}

		assertEquals(numFOs, numAdds);
	}

	@Tag("user_story_19279")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotFindAllFundingOpportunitiesRevisions() {
		assertThrows(AccessDeniedException.class, () -> foService.findAllFundingOpportunitiesRevisions());
	}

	@WithAnonymousUser
	@Test
	public void test_anonUserCanFindFundingOpportunitiesByAgency() {
		assertEquals(43, foService.findFundingOpportunitiesByAgency(agencyService.findAgencyById(1L)).size());
	}

	@Tag("user_story_14627")
	@WithAnonymousUser
	@Test
	public void test_anonUserCanFindResultsForGoldenListTable() {
		long foCount = foRepo.count();

		List<String[]> resultSet = foService.findGoldenListTableResults();
		String[] tableRow = resultSet.stream().filter(arr -> arr[0].equals("9")).findFirst().get();

		assertEquals(foCount, resultSet.size());
		assertArrayEquals(new String[] { "9", "Aboriginal Ambassadors in the Natural Sciences and Engineering (AANSE) (6610)",
				"Bourse pour ambassadeurs autochtones des sciences naturelles et du génie", "ICSP", "FR ICSP",
				"SP Secure Upload", "NAMIS" }, tableRow);
	}
	
	@Tag("user_story_19326")
	@WithAnonymousUser
	@Test
	public void test_findFundingOpportunityAndBU() {
		FundingOpportunity fo = foService.findFundingOpportunityEager(23L).get(0);
		
		assertEquals(3L, fo.getBusinessUnit().getId());
		assertThrows(DataRetrievalFailureException.class, () -> foService.findFundingOpportunityEager(Long.MAX_VALUE));
	}

}
