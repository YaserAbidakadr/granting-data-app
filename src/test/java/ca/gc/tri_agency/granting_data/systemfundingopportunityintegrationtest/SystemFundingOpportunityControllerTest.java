package ca.gc.tri_agency.granting_data.systemfundingopportunityintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.repo.SystemFundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;
import ca.gc.tri_agency.granting_data.service.SystemFundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class SystemFundingOpportunityControllerTest {

	@Autowired
	private SystemFundingOpportunityService sfoService;

	@Autowired
	private FundingOpportunityService foService;

	@Autowired
	private SystemFundingOpportunityRepository sfoRepo;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_14659")
	@Transactional
	@Rollback
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanUnlinkFoFromSfo_shouldRedirectToViewSystemFo() throws Exception {
		SystemFundingOpportunity sfo = sfoService.findSystemFundingOpportunityById(1L);
		FundingOpportunity linkedFO = sfo.getLinkedFundingOpportunity();

		assertNotNull(linkedFO);

		mvc.perform(MockMvcRequestBuilders.post("/admin/confirmUnlink").param("sfoId", "1")
				.param("foId", Long.toString(linkedFO.getId())).param("sfoName", sfo.getNameEn())
				.param("foName", linkedFO.getNameEn())).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/admin/viewSFO?id=1"));

		assertNull(sfoService.findSystemFundingOpportunityById(1L).getLinkedFundingOpportunity());

		// when the page is refreshed, the flash attribute should disappear
		mvc.perform(MockMvcRequestBuilders.get("/admin/confirmUnlink").param("sfoId", "1"))
				.andExpect(MockMvcResultMatchers.flash().attributeCount(0));
	}

	@Tag("user_story_14659")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_unlinkFoConfirmationPageAccessableByAdmin_shouldSucceedWith200() throws Exception {
		SystemFundingOpportunity sfo = sfoService.findSystemFundingOpportunityById(1L);
		String sfoName = sfo.getNameEn();
		String foName = foService.findFundingOpportunityName(sfo.getLinkedFundingOpportunity().getId()).getNameEn();

		assertTrue(mvc.perform(MockMvcRequestBuilders.get("/admin/confirmUnlink").param("sfoId", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString()
				.contains("Are you sure you want to unlink the System Funding Opportunity named " + sfoName
						+ " from the Funding Opportunity named " + foName + '?'));
	}

	@Tag("user_story_14659")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_unlinkFoConfirmationPageNotAccessableByNonAdminUsers_shouldReturn403() throws Exception {
		assertNotNull(sfoService.findSystemFundingOpportunityById(1L).getLinkedFundingOpportunity());

		mvc.perform(MockMvcRequestBuilders.get("/admin/confirmUnlink").param("sfoId", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Tag("user_story_14659")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Transactional
	@Rollback
	@Test
	public void test_confirmUnlinkPageNotAccessibleWhenSfoHasNoLinkedFo_shouldReturn404() throws Exception {
		SystemFundingOpportunity sfo = sfoService.findSystemFundingOpportunityById(2L);
		sfo.setLinkedFundingOpportunity(null);
		sfoService.saveSystemFundingOpportunity(sfo);

		assertNull(sfoService.findSystemFundingOpportunityById(2L).getLinkedFundingOpportunity());
		SystemFundingOpportunity unlinkedSfo = sfoRepo.findById(2L).get();
		unlinkedSfo.setLinkedFundingOpportunity(null);
		sfoRepo.save(unlinkedSfo);

		assertNull(sfoRepo.getOne(2L).getLinkedFundingOpportunity());
		assertTrue(mvc.perform(MockMvcRequestBuilders.get("/admin/confirmUnlink").param("sfoId", "2"))
				.andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResponse().getContentAsString()
				.contains("id=\"generalErrorPage\""));
	}

	@Tag("user_story_14659")
	@WithMockUser(roles = { "MDM ADMIN" })
	@Transactional
	@Rollback
	@Test
	public void test_unlinkFoBtnNotVisibleToAdminWhenSfoHasNoLinkedFo() throws Exception {
		SystemFundingOpportunity unlinkedSfo = sfoRepo.findById(2L).get();
		unlinkedSfo.setLinkedFundingOpportunity(null);
		sfoRepo.save(unlinkedSfo);

		assertNull(sfoRepo.getOne(2L).getLinkedFundingOpportunity());
		assertFalse(mvc.perform(MockMvcRequestBuilders.get("/admin/viewSFO").param("id", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString()
				.contains("id=\"unlinkSfoBtn\""));
	}

	@Tag("user_story_14659")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_unlinkFoBtnNotVisibleToNonAdminUsers() throws Exception {
		assertNotNull(sfoService.findSystemFundingOpportunityById(1L).getLinkedFundingOpportunity());
		assertFalse(mvc.perform(MockMvcRequestBuilders.get("/admin/viewSFO").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn().getResponse().getContentAsString()
				.contains("id=\"unlinkSfoBtn\""));
	}

	@Tag("user_story_14659")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_unlinkFoBtnVisibleToAdminWhenFoLinkedToSfo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/viewSFO").param("id", "1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"unlinkSfoBtn\"")));
	}

	@Tag("user_story_14592")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessAnalyzeSystemFOsPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/analyzeSFOs")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
				MockMvcResultMatchers.content().string(Matchers.containsString("id=\"analyzeSystemFundingOpportunitiesPage\"")));
	}

	@Tag("user_story_14592")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessAnalyzeSystemFundingOpportunitiesPage_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/analyzeSFOs")).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Tag("user_story_19301")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessAuditLogForAllSystemFundingOpportunities_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogSFO")).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		long numAdds = 0;
		Pattern regex = Pattern.compile("<td>ADD</td>");
		Matcher regexMatcher = regex.matcher(response);
		while (regexMatcher.find()) {
			++numAdds;
		}

		assertTrue(response.contains("id=\"systemFundingOpportunityAuditLogPage\""));
		assertEquals(numAdds, sfoRepo.count());
	}

	@Tag("user_story_19301")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessAuditLogForAllSystemFundingOpportunities_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogSFO")).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Tag("user_story_19301")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessAuditLogForOneSystemFundingOpportunity_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/admin/viewSFO").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		int numAdds = 0;

		Pattern regex = Pattern.compile("<td>ADD</td>");
		Matcher regexMatcher = regex.matcher(response);
		while (regexMatcher.find()) {
			++numAdds;
		}

		assertEquals(1, numAdds);
		assertTrue(response.contains("<span>Audit Log</span>"));
	}

}
