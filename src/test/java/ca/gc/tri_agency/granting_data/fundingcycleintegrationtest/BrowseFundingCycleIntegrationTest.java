package ca.gc.tri_agency.granting_data.fundingcycleintegrationtest;

import java.time.LocalDate;
import java.time.Period;
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
		final int plusMinusMonth = Period.between(LocalDate.now(), LocalDate.of(2021, 01, 01)).getMonths();

		Pattern startDateNoiRegex = Pattern.compile("class=\"cihr endDate\"");

		String response = mvc
				.perform(MockMvcRequestBuilders.get("/browse/viewCalendar")
						.param("plusMinusMonth", String.valueOf(plusMinusMonth)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"viewFundingCycleCalendarPage\"")))
				.andReturn().getResponse().getContentAsString();

		Matcher startDateNoiMatcher = startDateNoiRegex.matcher(response);

		int numMatches = 0;
		while (startDateNoiMatcher.find()) {
			++numMatches;
		}

		Assertions.assertEquals(2, numMatches, "The plusMinusMonth request param be the current month relative to January 2021;"
				+ " there are 2 Funding Cycles for CIHR that have a start date in January 2021.");
	}

	@WithAnonymousUser
	@Test
	public void test_anonUserCanAccessViewFcFromFyPage_shouldSucceedWith200() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewFCsForFY").param("fyId", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"viewFundingCyclesForFiscalYearPage\"")));
	}

}
