package ca.gc.tri_agency.granting_data.fundingopportunityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class FundingOpportunityControllerIntegrationTest {

	@Autowired
	private FundingOpportunityService foService;

	@Autowired
	private FundingOpportunityRepository foRepo;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19326")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanAccessEditFOPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/editFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"editFundingOpportunityPage\"")));
	}

	@Tag("user_story_19326")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessEditFOPage_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/editFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19326")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanEditFundingOpportunity_shouldSucceedWith302() throws Exception {
		long initFOCount = foRepo.count();
		FundingOpportunity initFO = foService.findFundingOpportunityById(1L);
		
		String nameFr = RandomStringUtils.randomAlphabetic(25);
		String frequency = RandomStringUtils.randomAlphabetic(10);

		mvc.perform(MockMvcRequestBuilders.post("/manage/editFo").param("id", "1")
				.param("frequency", frequency).param("nameFr", nameFr)
				.param("nameEn", "Collaborative Health Research Projects (CHRP) (5640)").param("division", "MCT")
				.param("_isJointInitiative", "on").param("_isComplex", "on").param("_isNOI", "on")
				.param("_isLOI", "on").param("businessUnit", "1"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/browse/viewFo?id=1"));

		FundingOpportunity editedFO = foService.findFundingOpportunityById(1L);
		
		assertEquals(initFOCount, foRepo.count());
		assertEquals(nameFr, editedFO.getNameFr());
		assertEquals(frequency, editedFO.getFrequency());
		assertEquals(initFO.getBusinessUnit().getId(), editedFO.getBusinessUnit().getId());
	}

	@Tag("user_story_19326")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotEditFundingOpportunity_shouldReturn403() throws Exception {
		long initFOCount = foRepo.count();

		String nameFr = RandomStringUtils.randomAlphabetic(25);
		String frequency = RandomStringUtils.randomAlphabetic(10);

		mvc.perform(MockMvcRequestBuilders.post("/manage/editFo").param("id", "1")
				.param("frequency", frequency).param("nameFr", nameFr)
				.param("nameEn", "Collaborative Health Research Projects (CHRP) (5640)").param("division", "MCT")
				.param("_isJointInitiative", "on").param("_isComplex", "on").param("_isNOI", "on")
				.param("_isLOI", "on").param("_participatingAgencies", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));

		assertEquals(initFOCount, foRepo.count());
		assertNotEquals(nameFr, foService.findFundingOpportunityById(1L).getNameFr());
		assertNotEquals(frequency, foService.findFundingOpportunityById(1L).getFrequency());

	}

}
