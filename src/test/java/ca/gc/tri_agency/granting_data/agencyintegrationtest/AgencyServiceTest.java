package ca.gc.tri_agency.granting_data.agencyintegrationtest;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.projection.AgencyProjection;
import ca.gc.tri_agency.granting_data.service.AgencyService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class AgencyServiceTest {

	@Autowired
	private AgencyService aService;

	@Tag("user_story_19401")
	@WithAnonymousUser
	@Test
	public void test_findResultsForBrowseViewAgency() {
		List<AgencyProjection> aProjections = aService.findResultsForBrowseViewAgency(1L);

		long numAgencies = aProjections.stream().filter(a -> a.getId().equals(new Long(1L))).count();

		Assertions.assertEquals(aProjections.size(), numAgencies, "Because this query is used to populate all 3 tables on"
				+ " /browse/viewAgency, it returns multiple rows however, each row should contain the same Agency.");
		
		Assertions.assertThrows(DataRetrievalFailureException.class, () -> aService.findResultsForBrowseViewAgency(Long.MAX_VALUE));
	}

}
