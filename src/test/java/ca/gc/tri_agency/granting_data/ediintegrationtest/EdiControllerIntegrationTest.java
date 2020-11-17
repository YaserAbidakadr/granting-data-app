package ca.gc.tri_agency.granting_data.ediintegrationtest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class EdiControllerIntegrationTest {

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19351")
	@WithAnonymousUser
	@Test
	public void test_ediVisualizationLinkVisibleOnHomePage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("href=\"browse/ediVisualization\"")));
	}

	@Tag("user_story_19351")
	@WithAnonymousUser
	@Test
	public void test_nonAdminUserCanAccessEdiVisualizationPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/ediVisualization")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"ediDataVisualizationPage\"")));
	}

	@Tag("user_story_19147")
	@WithMockUser(username = "aha")
	@Test
	public void test_authorizedEdiUserCanViewBuEdiData_shouldSucceedWith200() throws Exception {
		assertTrue(mvc.perform(MockMvcRequestBuilders.get("/manage/viewBuEdiData").param("buId", "13"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"buEdiDataPage\""))).andReturn()
				.getModelAndView().getModelMap().containsAttribute("ediMap"));
	}

	@Tag("user_story_19147")
	@WithMockUser(username = "aha")
	@Test
	public void test_unauthorizedEdiUseCannotViewBuEdiData_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/manage/viewBuEdiData").param("buId", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"forbiddenByRoleErrorPage\"")));
	}

	@Tag("user_story_19147")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminUserCanViewBuEdiData_shouldSucceedWith200() throws Exception {
		assertTrue(mvc.perform(MockMvcRequestBuilders.get("/manage/viewBuEdiData").param("buId", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"buEdiDataPage\""))).andReturn()
				.getModelAndView().getModelMap().containsAttribute("ediMap"));
	}

}
