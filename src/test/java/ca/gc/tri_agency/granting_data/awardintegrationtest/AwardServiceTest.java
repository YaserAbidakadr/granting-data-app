package ca.gc.tri_agency.granting_data.awardintegrationtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.repo.AwardRepository;
import ca.gc.tri_agency.granting_data.service.AwardService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class AwardServiceTest {

	@Autowired
	private AwardService awdService;

	@Autowired
	private AwardRepository awdRepo;

	@Tag("user_story_19410")
	@WithMockUser(username = "aha")
	@Test
	public void test_findAwardsForCurrentUser() {
		Assertions.assertEquals(4, awdService.findAwardsForCurrentUser().size());
	}

	@Tag("user_story_19414")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindAllAwards() {
		Assertions.assertEquals(awdRepo.count(), awdService.findAwardsForCurrentUser().size());
	}

}
