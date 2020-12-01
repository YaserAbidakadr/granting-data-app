package ca.gc.tri_agency.granting_data.fundingcycleintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

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
import ca.gc.tri_agency.granting_data.model.FundingCycle;
import ca.gc.tri_agency.granting_data.model.projection.FundingCycleProjection;
import ca.gc.tri_agency.granting_data.repo.FundingCycleRepository;
import ca.gc.tri_agency.granting_data.service.FiscalYearService;
import ca.gc.tri_agency.granting_data.service.FundingCycleService;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class FundingCycleServiceTest {

	@Autowired
	private FundingCycleService fcService;

	@Autowired
	private FiscalYearService fyService;

	@Autowired
	private FundingOpportunityService foService;

	@Autowired
	private FundingCycleRepository fcRepo;

	@WithAnonymousUser
	@Test
	public void test_findFundingCycleById() {
		assertNotNull(fcService.findFundingCycleById(1L));

		assertThrows(DataRetrievalFailureException.class, () -> fcService.findFundingCycleById(Long.MIN_VALUE));
	}

	@WithAnonymousUser
	@Test
	public void test_findAllFundingCycles() {
		assertTrue(0 < fcService.findAllFundingCycles().size());
	}

	@WithAnonymousUser
	@Test
	public void test_findFundingCyclesByFoId() {
		assertEquals(0, fcService.findFundingCyclesByFundingOpportunityId(Long.MAX_VALUE).size());
		assertTrue(0 < fcService.findFundingCyclesByFundingOpportunityId(1L).size());
	}

	@WithAnonymousUser
	@Test
	public void test_findFundingCyclesByFiscalYearId() {
		assertEquals(36, fcService.findFundingCyclesByFiscalYearId(3L).size());
	}

	@Tag("user_story_19201")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanCreateFundingCycle() {
		long initFCCount = fcRepo.count();

		FundingCycle fc = new FundingCycle();
		fc.setStartDate(LocalDate.of(2030, 1, 1));
		fc.setEndDate(LocalDate.of(2030, 1, 1));
		fc.setStartDateLOI(LocalDate.of(2030, 1, 1));
		fc.setEndDateLOI(LocalDate.of(2030, 1, 1));
		fc.setStartDateNOI(LocalDate.of(2030, 1, 1));
		fc.setEndDateNOI(LocalDate.of(2030, 1, 1));
		fc.setFiscalYear(fyService.findFiscalYearById(1L));
		fc.setFundingOpportunity(foService.findFundingOpportunityById(1L));
		fc.setIsOpen(false);
		fc.setExpectedApplications(100L);

		assertNotNull(fcService.saveFundingCycle(fc).getId());
		assertEquals(initFCCount + 1, fcRepo.count());
	}

	@Tag("user_story_19201")
	@WithMockUser(username = "jfs")
	@Test
	public void test_buProgramLeadCanCreateFundingCycle() {
		long initFCCount = fcRepo.count();

		FundingCycle fc1 = new FundingCycle();
		fc1.setIsOpen(true);
		fc1.setStartDate(LocalDate.of(2020, 1, 1));
		fc1.setExpectedApplications(1_000L);
		fc1.setFiscalYear(fyService.findFiscalYearById(4L));
		fc1.setFundingOpportunity(foService.findFundingOpportunityById(77L));

		// jfs can create a FC for FO id=77 since he's a Program Lead for BU id=2
		fcService.saveFundingCycle(fc1);
		assertEquals(initFCCount + 1, fcRepo.count());

		// jfs cannot create a FC for FO id=17 since he's a Program Officer for BU id=3
		FundingCycle fc2 = new FundingCycle();
		fc2.setFundingOpportunity(foService.findFundingOpportunityById(17L));
		assertThrows(AccessDeniedException.class, () -> fcService.saveFundingCycle(fc2));

		// jfs cannot create a FC for FO id=35 since he's not even a member of BU id=4
		FundingCycle fc3 = new FundingCycle();
		fc3.setFundingOpportunity(foService.findFundingOpportunityById(35L));
		assertThrows(AccessDeniedException.class, () -> fcService.saveFundingCycle(fc3));
	}

	@Tag("user_story_14750")
	@WithAnonymousUser
	@Test
	public void test_anonUserCanFindAllFCsForOneFO() {
		List<FundingCycleProjection> fcProjections = fcService.findFCsForBrowseViewFO(100L);

		assertEquals(1, fcProjections.size());
		assertEquals(9_162, fcProjections.get(0).getNumAppsExpected());
	}

	@Tag("user_story_19213")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanDeleteFC() {
		long initFCCount = fcRepo.count();

		fcService.deleteFundingCycleId(141L);

		assertEquals(initFCCount - 1, fcRepo.count());
	}

	@Tag("user_story_19213")
	@WithMockUser(username = "dev")
	@Test
	public void test_buProgramLeadCanDeleteFC() {
		long initFCCount = fcRepo.count();

		fcService.deleteFundingCycleId(127L);

		assertEquals(initFCCount - 1, fcRepo.count());
	}

	@Tag("user_story_19213")
	@WithMockUser(username = "dev")
	@Test
	public void test_buProgramOfficerCannotDeleteFC() {
		assertThrows(AccessDeniedException.class, () -> fcService.deleteFundingCycleId(11L));
	}

	@Tag("user_story_19213")
	@WithMockUser(username = "dev")
	@Test
	public void test_buNonMemberCannotDeleteFC() {
		assertThrows(AccessDeniedException.class, () -> fcService.deleteFundingCycleId(1L));
	}

	@Tag("user_story_19207")
	@WithMockUser(username = "dev")
	@Test
	public void test_buProgramLeadCanEditLinkedFC() {
		LocalDate newStartDate = LocalDate.now();

		FundingCycle fc = fcService.findFundingCycleById(13L);
		fc.setStartDate(newStartDate);
		fcService.saveFundingCycle(fc);

		assertEquals(newStartDate, fcService.findFundingCycleById(13L).getStartDate());
	}

	@Tag("user_story_19207")
	@WithMockUser(username = "dev")
	@Test
	public void test_buProgramOfficerCannotEditLinkedFC() {
		LocalDate newStartDate = LocalDate.now();

		FundingCycle fc = fcService.findFundingCycleById(41L);
		fc.setStartDate(newStartDate);

		assertThrows(AccessDeniedException.class, () -> fcService.saveFundingCycle(fc));
		assertNotEquals(newStartDate, fcService.findFundingCycleById(41L).getStartDate());
	}

	@Tag("user_story_19207")
	@WithMockUser(username = "dev")
	@Test
	public void test_buNonMemberCannotEditLinkedFC() {
		LocalDate newStartDate = LocalDate.now();

		FundingCycle fc = fcService.findFundingCycleById(102L);
		fc.setStartDate(newStartDate);

		assertThrows(AccessDeniedException.class, () -> fcService.saveFundingCycle(fc));
		assertNotEquals(newStartDate, fcService.findFundingCycleById(102L).getStartDate());
	}

	@Tag("user_story_19207")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanEditFC() {
		LocalDate newEndDate = LocalDate.now().plusYears(1);

		FundingCycle fc = fcService.findFundingCycleById(1L);
		fc.setEndDate(newEndDate);
		fcService.saveFundingCycle(fc);

		assertEquals(newEndDate, fcService.findFundingCycleById(1L).getEndDate());
	}

	@Tag("user_story_19213")
	@WithMockUser(username = "jfs")
	@Test
	public void test_buProgramLeadCanFindFundingCycleForConfirmDeleteFC() {
		FundingCycleProjection fcProjection = fcService.findFundingCycleForConfirmDeleteFC(49L);

		assertEquals(49L, fcProjection.getFundingOpportunityId());
		assertEquals(LocalDate.of(2024, 7, 4), fcProjection.getStartDate());
		assertEquals(7_593L, fcProjection.getNumAppsExpected());
	}

	@Tag("user_story_19213")
	@WithMockUser(username = "aha")
	@Test
	public void test_buProgramLeadCannotFindFundingCycleForConfirmDeleteFC() {
		assertThrows(AccessDeniedException.class, () -> fcService.findFundingCycleForConfirmDeleteFC(119L));
	}

	@Tag("user_story_19213")
	@WithMockUser(username = "aha")
	@Test
	public void test_buNonMemberCannotFindFundingCycleForConfirmDeleteFC() {
		assertThrows(AccessDeniedException.class, () -> fcService.findFundingCycleForConfirmDeleteFC(120L));
	}

	@Tag("user_story_19213")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindFundingCycleForConfirmDeleteFC() {
		assertNotNull(fcService.findFundingCycleForConfirmDeleteFC(2L));
	}

	@Tag("user_story_19229")
	@WithAnonymousUser
	@Test
	public void test_findFCsForCalendar() {
		final int plusMinusMonth = Period.between(LocalDate.now(), LocalDate.of(2020, 11, 1)).getMonths();

		List<FundingCycleProjection> fcProjections = fcService.findFundingCyclesForCalendar(plusMinusMonth);
		fcProjections.sort(Comparator.comparing(FundingCycleProjection::getStartDateNOI));
		
		assertEquals(2, fcProjections.size(),
				"The plusMinusMonth argument must be relative to November 2020 which has 2 FundingCycles.");
		assertEquals(LocalDate.of(2020, 11, 26), fcProjections.get(0).getStartDateNOI());
	}

}
