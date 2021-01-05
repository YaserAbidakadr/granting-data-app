package ca.gc.tri_agency.granting_data.systemfundingcycleintegrationtest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.service.SystemFundingCycleService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class SystemFundingCycleServiceTest {

	@Autowired
	private SystemFundingCycleService sfcService;
	
	@WithAnonymousUser
	@Test
	public void test_findSystemFundingCycleById_shouldSucceed() {
		assertNotNull(sfcService.findSystemFundingCycleById(1L));
	}

	@WithAnonymousUser
	@Test
	public void test_findSystemFundingCycleById_shouldThrowDataRetrievalFailureException() {
		assertThrows(DataRetrievalFailureException.class, () -> sfcService.findSystemFundingCycleById(Long.MAX_VALUE));
	}

	@Tag("user_story_14589")
	@WithAnonymousUser
	@Test
	public void test_findAllSystemFundingCycleExtIds() {
		assertTrue(10 <= sfcService.findAllSystemFundingCycleExtIds().size());
	}

	@WithAnonymousUser
	@Test
	public void test_findSFCsByFOid() {
		assertTrue(0 < sfcService.findSFCsBySFOid(1L).size());
	}

}
