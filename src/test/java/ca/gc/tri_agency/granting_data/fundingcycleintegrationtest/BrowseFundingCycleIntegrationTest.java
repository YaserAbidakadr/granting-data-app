package ca.gc.tri_agency.granting_data.fundingcycleintegrationtest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
public class BrowseFundingCycleIntegrationTest {

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19229")
	@WithAnonymousUser
	@Test
	public void test_anonUserCanAccessViewCalendarPage_shouldSucceedWith200() throws Exception {
		Pattern startDateNoiRegex = Pattern
				.compile(">Mitacs Elevate<\\/a>");

		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/viewCalendar").param("plusMinusMonth", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"viewFundingCycleCalendarPage\"")))
				.andReturn().getResponse().getContentAsString();
		
		Matcher startDateNoiMatcher = startDateNoiRegex.matcher(response);

		Assertions.assertTrue(startDateNoiMatcher.find(),
				"FundingCycle is not displayed in the calendar; at the beginning of every month, we have to adjust the"
						+ " plusMinusMonth request param so that it corresponds to November 2020");
	}

	@WithAnonymousUser
	@Test
	public void test_anonUserCanAccessViewFcFromFyPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewFcFromFy").param("fyId", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"viewFcFromFyPage\"")));
	}

}
