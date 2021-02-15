package ca.gc.tri_agency.granting_data.app.grantingstageintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.projection.GrantingStageProjection;
import ca.gc.tri_agency.granting_data.repo.GrantingStageRepository;
import ca.gc.tri_agency.granting_data.service.GrantingStageService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class GrantingStageServiceTest {
	
	@Autowired
	private GrantingStageService gStageService;
	
	@Autowired
	private GrantingStageRepository gStageRepo;
	
	@Test
	public void test_findGrantingStageById() {
		assertNotNull(gStageService.findGrantingStageById(1L));
		
		assertThrows(DataRetrievalFailureException.class, () -> gStageService.findGrantingStageById(Long.MAX_VALUE));
	}
	
	@Test
	public void test_findAllGrantingStages() {
		assertTrue(0 < gStageService.findAllGrantingStages().size());
	}
	
	@Tag("user_story_14572")
	@Test
	public void test_findAllGrantingStageNames() {
		List<GrantingStageProjection> gStages = gStageService.findAllGrantingStageNames();
		
		assertEquals(gStageRepo.count(), gStages.size());
		
		gStages.forEach(gStage -> {
			assertNotNull(gStage.getNameEn());
			assertNotNull(gStage.getNameFr());
		});
	}

}
