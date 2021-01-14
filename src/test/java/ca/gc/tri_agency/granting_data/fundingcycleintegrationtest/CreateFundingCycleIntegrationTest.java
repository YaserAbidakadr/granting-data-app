package ca.gc.tri_agency.granting_data.fundingcycleintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ca.gc.tri_agency.granting_data.repo.FundingCycleRepository;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class CreateFundingCycleIntegrationTest {

	@Autowired
	private FundingCycleRepository fcRepo;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19201")
	@WithMockUser(username = "dev")
	@Test
	public void test_buProgramLeadCanCreateFC_shouldSucceedWith302() throws Exception {
		long initFCCount = fcRepo.count();

		// verify "Create Funding Cycle" is visible to Program Lead
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "35")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(">Create Funding Cycle</a>")));

		// verify Program Lead can access createFC page
		mvc.perform(MockMvcRequestBuilders.get("/manage/createFC").param("foId", "35")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"createFundingCyclePage\"")));

		// verify Program Lead can create a FundingCycle
		assertTrue(mvc.perform(MockMvcRequestBuilders.post("/manage/createFC").param("foId", "35").param("open", "false")
				.param("fundingOpportunity", "35").param("fiscalYear", "2").param("startDate", "2017-01-01")
				.param("endDate", "2017-12-31").param("expectedApplications", "99"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/manage/manageFo?id=35")).andReturn().getFlashMap()
				.containsValue("Successfully Created a Funding Cycle for this Funding Opportunity"),
				"Created FundingCycle flash attribute is missing");

		assertEquals(initFCCount + 1, fcRepo.count());
	}

	@Tag("user_story_19201")
	@WithMockUser(username = "dev")
	@Test
	public void test_buProgramOfficerCannotCreateFC_shouldReturn403() throws Exception {
		long initFCCount = fcRepo.count();

		// verify "Create Funding Cycle" button is NOT visible to a Program Officer
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "41")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.not(Matchers.containsString(">Create Funding Cycle</a>"))));

		// verify Program Officer cannot access createFC page
		mvc.perform(MockMvcRequestBuilders.get("/manage/createFC").param("foId", "41"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// verify Program Officer cannot create a FundingCycle
		mvc.perform(MockMvcRequestBuilders.post("/manage/createFC").param("foId", "41").param("fundingOpportunity", "41")
				.param("open", "true").param("expectedApplications", "99").param("fiscalYear", "1")
				.param("startDate", "2016-01-31")).andExpect(MockMvcResultMatchers.status().isForbidden());

		assertEquals(initFCCount, fcRepo.count());
	}

	@Tag("user_story_19201")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanCreateFC_shouldSucceedWith302() throws Exception {
		long initFCCount = fcRepo.count();

		// verify "Create Funding Cycle" button is visible to Admin
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(">Create Funding Cycle</a>")));

		// verify Admin can access createFC page
		mvc.perform(MockMvcRequestBuilders.get("/manage/createFC").param("foId", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"createFundingCyclePage\"")));

		// verify Admin can create a FundingCycle
		assertTrue(mvc.perform(MockMvcRequestBuilders.post("/manage/createFC").param("foId", "1").param("fiscalYear", "1")
				.param("open", "true").param("expectedApplications", "123").param("fundingOpportunity", "1")
				.param("startDate", "2021-01-01").param("businessUnit", "2"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/manage/manageFo?id=1")).andReturn().getFlashMap()
				.containsValue("Successfully Created a Funding Cycle for this Funding Opportunity"),
				"Created FundingCycle flash attribute is missing");

		assertEquals(initFCCount + 1, fcRepo.count());
	}

	@Tag("user_story_19201")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_createFcFormValidationErrMsgs_shouldReturn200() throws Exception {
		long initFcCount = fcRepo.count();

		// foId request parameter is required for the URL
		String response = mvc.perform(MockMvcRequestBuilders.post("/manage/createFC").param("foId", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		assertTrue(response.contains("id=\"createFundingCyclePage\""));
		assertTrue(response.contains("The form could not be submitted because 4 errors were found."));

		assertEquals(initFcCount, fcRepo.count());
	}

}
