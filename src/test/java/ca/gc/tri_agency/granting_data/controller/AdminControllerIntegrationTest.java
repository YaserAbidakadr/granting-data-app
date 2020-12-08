package ca.gc.tri_agency.granting_data.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.repo.MemberRoleRepository;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class AdminControllerIntegrationTest {

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private FundingOpportunityRepository foRepo;
	
	@Autowired
	private MemberRoleRepository mrRepo;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminUserCannotAddFundingOpportunities_shouldFailWith403() throws Exception {
		long numFos = foRepo.count();

		mvc.perform(post("/admin/createFo").param("id", "26").param("nameEn", "ACE").param("nameFr", "BDF")
				.param("division", "Q").param("isJointIntiative", "false")
				.param("_isJointIntiative", "on").param("partnerOrg", "Z").param("isComplex", "false")
				.param("_isComplex", "on").param("isEdiRequired", "false").param("_isEdiRequired", "on")
				.param("fundingType", "E").param("frequency", "Once").param("isNOI", "false").param("_isNOI", "on")
				.param("isLOI", "false").param("_isLOI", "on")).andExpect(status().isForbidden())
				.andExpect(content().string(containsString("id=\"forbiddenByRoleErrorPage\"")));

		// verify that a FO was not added
		assertEquals(numFos, foRepo.count());
	}

	@Tag("user_story_14593")
	@WithAnonymousUser
	@Test
	public void test_anonUserCannotAddFundingOpportunities_shouldRedirectToLoginWith302() throws Exception {
		long numFos = foRepo.count();

		mvc.perform(post("/admin/createFo").param("id", "26").param("nameEn", "A").param("nameFr", "B")
				.param("division", "Q").param("isJointIntiative", "false")
				.param("_isJointIntiative", "on").param("partnerOrg", "Z").param("isComplex", "false")
				.param("_isComplex", "on").param("isEdiRequired", "false").param("_isEdiRequired", "on")
				.param("fundingType", "E").param("frequency", "Once").param("isNOI", "false").param("_isNOI", "on")
				.param("isLOI", "false").param("_isLOI", "on")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/login"));

		// verify that a FO was not added
		assertEquals(numFos, foRepo.count());
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = { "MDM ADMIN" })
	@Test
	public void test_onlyAdminCanAddFundingOpportunities_shouldSucceedWith302() throws Exception {
		long numFos = foRepo.count();

		mvc.perform(post("/admin/createFo").param("nameEn", "ABC").param("nameFr", "BCD").param("division", "Q")
				.param("isJointIntiative", "false").param("_isJointIntiative", "on").param("partnerOrg", "Z")
				.param("isComplex", "false").param("_isComplex", "on").param("isEdiRequired", "false")
				.param("_isEdiRequired", "on").param("fundingType", "E").param("frequency", "Once")
				.param("isNOI", "false").param("_isNOI", "on").param("isLOI", "false").param("_isLOI", "on"))
				.andExpect(status().is3xxRedirection()).andExpect(MockMvcResultMatchers.redirectedUrl("/browse/fundingOpportunities"));

		// verify that a FO was added
		assertEquals(numFos + 1, foRepo.count());
	}

	@Tag("web_configuration_test")
	@WithAnonymousUser
	@Test
	public void givenAnonymousRequestOnAdminUrl_shouldFailWith301() throws Exception {
		mvc.perform(get("/admin/home").contentType(MediaType.APPLICATION_XHTML_XML)).andExpect(status().is3xxRedirection());
	}

	@Tag("web_configuration_test")
	@WithMockUser(username = "sshrc-user", roles = { "SSHRC" })
	@Test
	public void givenSshrcRequestOnAdminUrl_shouldFailWithForbiddenByRoleError() throws Exception {
		mvc.perform(get("/admin/home").contentType(MediaType.APPLICATION_XHTML_XML))
				.andExpect(content().string(containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("web_configuration_test")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void givenAdminAuthRequestOnAdminUrl_shouldSucceedWith200() throws Exception {
		mvc.perform(get("/admin/home").contentType(MediaType.APPLICATION_XHTML_XML)).andExpect(status().isOk());
	}

	@Tag("user_story_19290")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessAuditLogForAllMemberRoles_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogMR"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		long numAdds = 0;
		Pattern addsRegex = Pattern.compile("<td>ADD</td>");
		Matcher addsMatcher = addsRegex.matcher(response);
		while (addsMatcher.find()) {
			++numAdds;
		}
		
		long numDels = 0;
		Pattern delsRegex = Pattern.compile("<td>DEL</td>");
		Matcher delsMatcher = delsRegex.matcher(response);
		while (delsMatcher.find()) {
			++numDels;
		}

		assertTrue(response.contains("id=\"memberRoleAuditLogPage\""));
		assertTrue(response.contains("<title>Audit Log - Member Roles</title>"));
		assertTrue(response.contains("href=\"/admin/auditLogs\""));
		assertEquals(mrRepo.count(), numAdds - numDels);
	}

	@Tag("user_story_19290")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessAuditLogForAllMemberRoles_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogMR")).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19290")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanAccessAuditLogForOneMemberRole_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogMR").param("id", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		int numAdds = 0;

		Pattern regex = Pattern.compile("<td>ADD</td>");
		Matcher regexMatcher = regex.matcher(response);
		while (regexMatcher.find()) {
			++numAdds;
		}

		assertTrue(response.contains("id=\"memberRoleAuditLogPage\""));
		assertTrue(response.contains("<title>Audit Log - Member Role</title>"));
		assertTrue(response.contains("href=\"/browse/viewBU?id=1\""));
		assertEquals(1, numAdds);
	}

	@Tag("user_story_19290")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessAuditLogForOneMemberRole_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogMR").param("id", "2")).andExpect(
				MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19279")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanAccessAuditLogForAllFundingOpportunites_shouldSucceedWith200() throws Exception {
		long numFOs = foRepo.count();

		String response = mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogFO"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		long numAdds = 0;

		Pattern regex = Pattern.compile("<td>ADD</td>");
		Matcher regexMatcher = regex.matcher(response);
		while (regexMatcher.find()) {
			++numAdds;
		}

		assertTrue(response.contains("id=\"fundingOpportunityAuditLogPage\""));
		assertEquals(numFOs, numAdds);
	}

	@Tag("user_story_19279")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessAuditLogForAllFundingOpportunities_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/auditLogFO")).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19279")
	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanAccessAuditLogForOneFundingOpportunity_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/viewFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		int numAdds = 0;

		Pattern regex = Pattern.compile("<td>ADD</td>");
		Matcher regexMatcher = regex.matcher(response);
		while (regexMatcher.find()) {
			++numAdds;
		}

		assertTrue(response.contains("<span>Audit Log</span>"));
		assertEquals(1, numAdds);
	}

	@Tag("user_story_19279")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotAccessAuditLogForOneFundingOpportunity_shouldReturn200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/viewFo").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		int numAdds = 0;

		Pattern regex = Pattern.compile("<td>ADD</td>");
		Matcher regexMatcher = regex.matcher(response);
		while (regexMatcher.find()) {
			++numAdds;
		}

		assertFalse(response.contains("<h3>Audit Log</h3>"));
		assertEquals(0, numAdds);
	}
}
