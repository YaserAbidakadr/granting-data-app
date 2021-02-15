package ca.gc.tri_agency.granting_data.applicationparticipationintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
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

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class BrowseApplicationParticipationIntegrationTest {

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	private void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19154")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanBrowseAllAppParts_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/appParticipations"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"appParticipationsPage\"")))
				.andReturn().getResponse().getContentAsString();

		Pattern regex = Pattern.compile("<tr>");
		Matcher regexMatcher = regex.matcher(response);

		int numRows = 0;
		while (regexMatcher.find()) {
			++numRows;
		}

		// 17 - 18 rows (b/c another test creates an AppPart) for AppPart data + 1 row for the table header
		assertTrue(numRows >= 18 && numRows <= 19);
	}

	@Tag("user_story_19154")
	@WithMockUser(username = "aha")
	@Test
	public void test_nonAdminCanBrowseAuthorizedAppParts_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/appParticipations"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"appParticipationsPage\"")))
				.andReturn().getResponse().getContentAsString();

		Pattern regex = Pattern.compile("<tr>");
		Matcher regexMatcher = regex.matcher(response);

		int numRows = 0;
		while (regexMatcher.find()) {
			++numRows;
		}

		// 11 rows for AppPart data + 1 row for the table header
		assertEquals(12, numRows);
	}

	@Tag("user_story_19154")
	@WithMockUser(username = "aha")
	@Test
	public void test_buMemberCanViewOneLinkedAppPart_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewAP").param("id", "2")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"viewOneAppPartPage\"")))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Stevie Satterfield")));
	}

	@Tag("user_story_19154")
	@WithMockUser(username = "aha")
	@Test
	public void test_nonBuMemberCannotViewOneLinkedAppPart_shouldReturn403() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewAP").param("id", "3"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

}
