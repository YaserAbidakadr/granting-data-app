package ca.gc.tri_agency.granting_data.fiscalyearintegrationtest;

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
import ca.gc.tri_agency.granting_data.repo.FiscalYearRepository;
import ca.gc.tri_agency.granting_data.service.FiscalYearService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class CreateFiscalYearIntegrationTest {

	@Autowired
	private FiscalYearService fyService;

	@Autowired
	private FiscalYearRepository fyRepo;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19333")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_createFYLinkVisibleToAdmin_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewFYs")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"createFiscalYearLink\"")));
	}

	@Tag("user_story_19333")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_createFYLinkNotVisibleToNonAdmin_shouldReturn200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewFYs")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
				MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("id=\"createFiscalYearLink\""))));
	}

	@Tag("user_story_19333")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessCreateFYPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/createFY")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"createFiscalYearPage\"")));
	}

	@Tag("user_story_19333")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessCreateFYPage_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/createFY")).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19333")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanCreateFY_shouldSucceedWith302() throws Exception {
		long initFYCount = fyRepo.count();

		String year = "2025";

		mvc.perform(MockMvcRequestBuilders.post("/manage/createFY").param("year", year))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/browse/viewFYs"));

		assertTrue(fyService.findFiscalYearByYear(new Long(year)).isPresent());
		assertEquals(initFYCount + 1, fyRepo.count());
	}

	@Tag("user_story_19333")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateFY_shouldReturn403() throws Exception {
		long initFYCount = fyRepo.count();

		mvc.perform(MockMvcRequestBuilders.post("/manage/createFY").param("year", "2025"))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));

		assertEquals(initFYCount, fyRepo.count());
	}

	@Tag("user_story_19333")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_createFormValidation_shouldReturn200() throws Exception {
		long initFYCount = fyRepo.count();
		
		mvc.perform(MockMvcRequestBuilders.post("/manage/createFY").param("year", String.valueOf(Integer.MAX_VALUE)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("Year must be between 1999 and 2050")));

		mvc.perform(MockMvcRequestBuilders.post("/manage/createFY").param("year", "2020"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("That year already exists")));
		
		assertEquals(initFYCount, fyRepo.count());
	}
}
