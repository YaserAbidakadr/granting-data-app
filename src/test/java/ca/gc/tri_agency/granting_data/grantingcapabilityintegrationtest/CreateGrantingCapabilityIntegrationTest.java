package ca.gc.tri_agency.granting_data.grantingcapabilityintegrationtest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.repo.GrantingCapabilityRepository;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class CreateGrantingCapabilityIntegrationTest {

	@Autowired
	private GrantingCapabilityService gcService;

	@Autowired
	private GrantingCapabilityRepository gcRepo;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_14572")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_createGCLinkVisibleToAdmin_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"createGrantingCapabilityLink\"")));
	}

	@Tag("user_story_14572")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_createGCLinkNotVisibleToNonAdmin_shouldReturn200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.not(Matchers.containsString("id=\"createGrantingCapabilityLink\""))));
	}

	@Tag("user_story_14572")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanAccessCreateGCPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/addGrantingCapabilities").param("foId", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"createGrantingCapabilityPage\"")));
	}

	@Tag("user_story_14572")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessCreateGCPage_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/addGrantingCapabilities").param("foId", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_14572")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanCreateGC_shouldSucceedWith302() throws Exception {
		long initGCCount = gcRepo.count();

		String description = RandomStringUtils.randomAlphabetic(50);
		String url = "https://www." + RandomStringUtils.randomAlphabetic(25) + ".com";

		mvc.perform(MockMvcRequestBuilders.post("/manage/addGrantingCapabilities").param("description", description)
				.param("url", url).param("fundingOpportunity", "1").param("grantingStage", "1").param("grantingSystem", "1"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/manage/manageFo?id=1"))
				.andExpect(MockMvcResultMatchers.flash().attribute("actionMsg", "Successfully Added New Granting Capability"));

		mvc.perform(MockMvcRequestBuilders.get("/browse/viewFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.flash().attributeCount(0));

		long newGCCount = gcRepo.count();
		assertEquals(initGCCount + 1, newGCCount);

		GrantingCapability newGc = gcService.findAllGrantingCapabilities().get((int) newGCCount - 1);
		assertEquals(description, newGc.getDescription());
		assertEquals(url, newGc.getUrl());
	}

	@Tag("user_story_14572")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateGC_shouldReturn403() throws Exception {
		long initGCCount = gcRepo.count();

		mvc.perform(MockMvcRequestBuilders.post("/manage/addGrantingCapabilities").param("foId", "1").param("description", "test")
				.param("url", "www.test.com").param("fundingOpportunity", "1").param("grantingStage", "1")
				.param("grantinSystem", "1")).andExpect(MockMvcResultMatchers.status().isForbidden());

		assertEquals(initGCCount, gcRepo.count());
	}

	@Tag("user_story_14572")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_verifyCreateGcFormValidationErrMsgs_shouldReturn200() throws Exception {
		long initGcCount = gcRepo.count();

		String response = mvc.perform(MockMvcRequestBuilders.post("/manage/addGrantingCapabilities").param("fundingOpportunity", "1")
				.param("url", "ZZZZ"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		assertTrue(response.contains("id=\"createGrantingCapabilityPage\""));
		assertTrue(response.contains("The form could not be submitted because 4 errors were found."));

		assertEquals(initGcCount, gcRepo.count());
	}

}
