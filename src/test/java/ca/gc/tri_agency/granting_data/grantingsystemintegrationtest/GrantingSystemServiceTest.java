package ca.gc.tri_agency.granting_data.grantingsystemintegrationtest;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.projection.GrantingSystemProjection;
import ca.gc.tri_agency.granting_data.repo.GrantingSystemRepository;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class GrantingSystemServiceTest {
	
	@Autowired
	private GrantingSystemRepository gSysRepo;
	
	@Autowired
	private GrantingSystemService gSysService;
	
	@Tag("user_story_14572")
	@Test
	public void test_findAllGrantingSystemAcronyms() {
		List<GrantingSystemProjection> gSysList = gSysService.findAllGrantingSystemAcronyms();
		
		assertEquals(gSysRepo.count(), gSysList.size());
		
		gSysList.forEach(gSys -> {
			assertNotNull(gSys.getAcronym());
		});
	}

}
