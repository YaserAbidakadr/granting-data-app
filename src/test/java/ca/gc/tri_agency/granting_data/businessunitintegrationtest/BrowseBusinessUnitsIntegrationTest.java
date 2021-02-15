package ca.gc.tri_agency.granting_data.businessunitintegrationtest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
public class BrowseBusinessUnitsIntegrationTest {

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19401")
	@WithAnonymousUser
	@Test
	public void test_viewBULinkVisibleOnViewAgencyPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewAgency").param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"viewBULink\"")));
	}

	@Tag("user_story_19401")
	@WithAnonymousUser
	@Test
	public void test_anonUserCanAccessViewBUPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewBU").param("id", "2")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"viewBUPage\"")))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("rpp@nserc-crsng.gc.ca")));
	}

}
