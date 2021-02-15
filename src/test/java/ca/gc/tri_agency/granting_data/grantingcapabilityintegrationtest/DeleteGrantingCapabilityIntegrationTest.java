package ca.gc.tri_agency.granting_data.grantingcapabilityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

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
import ca.gc.tri_agency.granting_data.repo.GrantingCapabilityRepository;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class DeleteGrantingCapabilityIntegrationTest {

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

	@Tag("user_story_19005")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_deleteGCLinkVisibleToAdmin_shouldSucceedWith200() throws Exception {
		long numGCs = gcService.findGrantingCapabilitiesByFoId(1L).size();
		assertTrue(numGCs > 0);

		String response = mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Pattern regex = Pattern.compile("\n");
		long numDeleteGCLinks = regex.splitAsStream(response)
				.filter(line -> line.contains("id=\"deleteGrantingCapabilityLink\"")).count();

		assertEquals(numGCs, numDeleteGCLinks);
	}

	@Tag("user_story_19005")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_deleteGCLinkNotVisibleToNonAdmin_shouldReturn200() throws Exception {
		long numGCs = gcService.findGrantingCapabilitiesByFoId(1L).size();
		assertTrue(numGCs > 0);

		String response = mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Pattern regex = Pattern.compile("\n");
		long numDeleteGCLinks = regex.splitAsStream(response)
				.filter(line -> line.contains("id=\"deleteGrantingCapabilityLink\"")).count();

		assertNotEquals(numGCs, numDeleteGCLinks);
		assertEquals(0L, numDeleteGCLinks);
	}

	@Tag("user_story_19005")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessDeleteGCPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/deleteGC").param("id", "102"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"deleteGrantingCapabilityPage\"")));
	}

	@Tag("user_story_19005")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessDeleteGCPage_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/deleteGC").param("id", "102"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Tag("user_story_19005")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanDeleteGC_shouldSucceedWith302() throws Exception {
		long numGCs = gcRepo.count();

		String foId = String.valueOf(gcService.findGrantingCapabilityById(103L).getFundingOpportunity().getId());

		mvc.perform(MockMvcRequestBuilders.post("/manage/deleteGC").param("id", "103").param("foId", foId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/manage/manageFo?id=" + foId))
				.andExpect(MockMvcResultMatchers.flash().attribute("actionMsg",
						"Successfully Deleted Granting Capability"));

		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", foId))
				.andExpect(MockMvcResultMatchers.flash().attributeCount(0));

		assertEquals(numGCs - 1, gcRepo.count());
	}

	@Tag("user_story_19005")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotDeleteGC_shouldReturn403() throws Exception {
		long numGCs = gcRepo.count();

		mvc.perform(MockMvcRequestBuilders.post("/manage/deleteGC").param("id", "104").param("foId", "106"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		assertEquals(numGCs, gcRepo.count());
	}

}
