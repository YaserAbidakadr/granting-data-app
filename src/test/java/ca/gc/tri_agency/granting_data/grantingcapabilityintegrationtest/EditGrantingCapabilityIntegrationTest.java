package ca.gc.tri_agency.granting_data.grantingcapabilityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

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
public class EditGrantingCapabilityIntegrationTest {

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private GrantingCapabilityRepository gcRepo;

	@Autowired
	private GrantingCapabilityService gcService;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19004")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_editGCLinkVisibleToAdmin_shouldSucceedWith200() throws Exception {
		long numGCs = gcService.findGrantingCapabilitiesByFoId(1L).size();
		assertTrue(numGCs > 0);

		String response = mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Pattern regex = Pattern.compile("\n");
		long numEditGCLinks = regex.splitAsStream(response).filter(line -> line.contains("href=\"editGC?id=")).count();

		assertEquals(numGCs, numEditGCLinks);
	}

	@Tag("user_story_19004")
	@WithMockUser(username = "admin", roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_editGCLinkNotVisibleToNonAdmin_shouldReturn200() throws Exception {
		long numGCs = gcService.findGrantingCapabilitiesByFoId(1L).size();
		assertTrue(numGCs > 0);

		String response = mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Pattern regex = Pattern.compile("\n");
		long numEditGCLinks = regex.splitAsStream(response).filter(line -> line.contains("href=\"editGC?id=")).count();

		assertNotEquals(numGCs, numEditGCLinks);
		assertEquals(0, numEditGCLinks);
	}

	@Tag("user_story_19004")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessEditGCPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/editGC").param("id", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"editGrantingCapabilityPage\"")));
	}

	@Tag("user_story_19004")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessEditGCPage_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/editGC").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19004")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanEditGC_shouldSucceedWith302() throws Exception {
		long initGcRepoCount = gcRepo.count();

		String description = RandomStringUtils.randomAlphabetic(10);
		String url = "http://www" + RandomStringUtils.randomAlphabetic(10) + ".ca";
		String gStageId = "1";
		String gSystemId = "1";
		String foId = gcService.findGrantingCapabilityById(1L).getFundingOpportunity().getId().toString();

		mvc.perform(MockMvcRequestBuilders.post("/manage/editGC").param("id", "1").param("description", description).param("url", url)
				.param("fundingOpportunity",
						String.valueOf(gcService.findGrantingCapabilityById(1L).getFundingOpportunity().getId()))
				.param("grantingStage", gStageId).param("grantingSystem", gSystemId).param("fundingOpportunity", foId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/manage/manageFo?id="
						+ String.valueOf(gcService.findGrantingCapabilityById(1L).getFundingOpportunity().getId())))
				.andExpect(MockMvcResultMatchers.flash().attribute("actionMsg", "Granting Capability Successfully Updated"));

		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id",
				String.valueOf(gcService.findGrantingCapabilityById(1L).getFundingOpportunity().getId())))
				.andExpect(MockMvcResultMatchers.flash().attributeCount(0));

		GrantingCapability gcAfterUpdate = gcService.findGrantingCapabilityById(1L);

		assertEquals(description, gcAfterUpdate.getDescription());
		assertEquals(url, gcAfterUpdate.getUrl());
		assertEquals(gStageId, String.valueOf(gcAfterUpdate.getGrantingStage().getId()));
		assertEquals(gSystemId, String.valueOf(gcAfterUpdate.getGrantingSystem().getId()));
		assertEquals(foId, gcAfterUpdate.getFundingOpportunity().getId().toString());

		assertEquals(initGcRepoCount, gcRepo.count());
	}

	@Tag("user_story_19004")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotEditGC_shouldReturn403() throws Exception {
		long initGcRepoCount = gcRepo.count();

		String description = RandomStringUtils.randomAlphabetic(10);
		String url = "www" + RandomStringUtils.randomAlphabetic(10) + ".ca";
		String gStageId = "0";
		String gSystemId = "0";

		mvc.perform(MockMvcRequestBuilders.post("/manage/editGC").param("id", "1").param("description", description).param("url", url)
				.param("fundingOpportunity",
						String.valueOf(gcService.findGrantingCapabilityById(1L).getFundingOpportunity().getId()))
				.param("grantingStage", gStageId).param("grantingSystem", gSystemId))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));

		GrantingCapability gcAfterFailedUpdate = gcService.findGrantingCapabilityById(1L);

		assertNotEquals(description, gcAfterFailedUpdate.getDescription());
		assertNotEquals(url, gcAfterFailedUpdate.getUrl());
		assertNotEquals(gStageId, String.valueOf(gcAfterFailedUpdate.getGrantingStage().getId()));
		assertNotEquals(gSystemId, String.valueOf(gcAfterFailedUpdate.getGrantingSystem().getId()));

		assertEquals(initGcRepoCount, gcRepo.count());
	}

	@Tag("user_story_19004")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_verifyEditGcFormValidationErrMsgs_shouldReturn200() throws Exception {
		final Long gcId = 1L;

		GrantingCapability gcBefore = gcService.findGrantingCapabilityById(gcId);

		// the fundingOpportunity param corresponds to a hidden input
		String response = mvc.perform(MockMvcRequestBuilders.post("/manage/editGC").param("id", gcId.toString())
				.param("fundingOpportunity", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		GrantingCapability gcAfter = gcService.findGrantingCapabilityById(gcId);

		System.out.println(response);
		
		assertTrue(response.contains("id=\"editGrantingCapabilityPage\""));
		assertTrue(response.contains("The form could not be submitted because 4 errors were found."));

		assertEquals(gcBefore.getDescription(), gcAfter.getDescription());
		assertEquals(gcBefore.getUrl(), gcAfter.getUrl());
		assertEquals(gcBefore.getFundingOpportunity().getId(), gcAfter.getFundingOpportunity().getId());
		assertEquals(gcBefore.getGrantingSystem().getId(), gcAfter.getGrantingSystem().getId());
		assertEquals(gcBefore.getGrantingStage().getId(), gcAfter.getGrantingStage().getId());
	}

}
