package ca.gc.tri_agency.granting_data.awardintegrationtest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
import ca.gc.tri_agency.granting_data.repo.AwardRepository;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class BrowseAwardIntegrationTest {

	@Autowired
	private AwardRepository awdRepo;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19410")
	@WithMockUser(username = "aha")
	@Test
	public void test_nonAdminUserCanAccessBrowseAwards_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/awards")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"awardsPage\""))).andReturn()
				.getResponse().getContentAsString();

		int numFYrs = 0;

		Pattern fYrTableCellRegex = Pattern.compile("<td>201[7|8]<\\/td>");
		Matcher fYrtableCellMatcher = fYrTableCellRegex.matcher(response);
		while (fYrtableCellMatcher.find()) {
			++numFYrs;
		}

		Assertions.assertTrue(numFYrs < awdRepo.count(), "User aha should not be able to see all of the Awards");
		Assertions.assertEquals(4, numFYrs, "User aha should not be able to see all of the Awards");
	}

	@Tag("user_story_19414")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanAccessAllAwards_shouldSucceedWith200() throws Exception {
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/awards")).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		Assertions.assertTrue(response.contains("id=\"awardsPage\""));

		int numEntries = 0;

		// each table entry must contain a year between 1999 and 2050
		Pattern fYrRegex = Pattern.compile("<td>(1999|20([0-4][0-9]|50))</td>");
		Matcher fyMatcher = fYrRegex.matcher(response);
		while (fyMatcher.find()) {
			++numEntries;
		}

		Assertions.assertEquals(awdRepo.count(), numEntries, "Admin should be able to access all the Awards");
	}
}
