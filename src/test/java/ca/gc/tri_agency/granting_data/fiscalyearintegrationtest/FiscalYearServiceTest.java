package ca.gc.tri_agency.granting_data.fiscalyearintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.FiscalYear;
import ca.gc.tri_agency.granting_data.model.projection.FiscalYearProjection;
import ca.gc.tri_agency.granting_data.repo.FiscalYearRepository;
import ca.gc.tri_agency.granting_data.service.FiscalYearService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class FiscalYearServiceTest {

	@Autowired
	private FiscalYearService fyService;

	@Autowired
	private FiscalYearRepository fyRepo;
	
	@Tag("user_story_19201")
	@WithAnonymousUser
	@Test
	public void test_findFiscalYearById() {
		assertNotNull(fyService.findFiscalYearById(1L));
		
		assertThrows(DataRetrievalFailureException.class, () -> fyService.findFiscalYearById(Long.MAX_VALUE));
	}

	@Tag("user_story_19201")
	@Tag("user_story_19345")
	@WithAnonymousUser
	@Test
	public void test_findAllFiscalYearsOrderedByYear() {
		assertTrue(4 <= fyService.findAllFiscalYearProjectionsOrderByYear().size());
	}

	@Tag("user_story_19333")
	@WithAnonymousUser
	@Test
	public void test_findFiscalYearByYear() {
		assertTrue(fyService.findFiscalYearByYear(2020L).isPresent());
		assertFalse(fyService.findFiscalYearByYear(Long.MAX_VALUE).isPresent());
	}

	@Tag("user_story_19333")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanCreateFiscalYear() {
		long initFYCount = fyRepo.count();

		FiscalYear newFy = new FiscalYear(2040L);
		newFy = fyService.saveFiscalYear(newFy);

		assertNotNull(newFy.getId());
		assertEquals(initFYCount + 1, fyRepo.count());
		
		assertThrows(DataIntegrityViolationException.class, () -> fyService.saveFiscalYear(new FiscalYear(2040L)));
	}

	@Tag("user_story_19333")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateFiscalYear() {
		assertThrows(AccessDeniedException.class, () -> fyService.saveFiscalYear(new FiscalYear(2041L)));
	}
	
	@Tag("user_story_19345")
	@WithAnonymousUser
	@Test
	public void test_findNumAppsExpectedForEachFiscalYear() {
		List<FiscalYearProjection> results = fyService.findNumAppsExpectedForEachFiscalYear();
		
		assertEquals(4, results.size());
		assertEquals(159_845L, results.get(2).getNumAppsReceived());
	}
}
