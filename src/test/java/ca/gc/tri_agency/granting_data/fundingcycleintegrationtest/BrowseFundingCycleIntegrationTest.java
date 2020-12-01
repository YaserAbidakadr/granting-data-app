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
import ca.gc.tri_agency.granting_data.service.FundingCycleService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class BrowseFundingCycleIntegrationTest {

	@Autowired
	private WebApplicationContext ctx;
	
	@Autowired
	private FundingCycleService fcService;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19229")
	@WithAnonymousUser
	@Test
	public void test_anonUserCanAccessViewCalendarPage_shouldSucceedWith200() throws Exception {
		final int plusMinusMonth = Period.between(LocalDate.now(), LocalDate.of(2021, 1, 1)).getMonths();
		
		Pattern startDateNoiRegex = Pattern.compile("class=\"cihr endDate\"");

		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/viewCalendar").param("plusMinusMonth", String.valueOf(plusMinusMonth)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("id=\"viewFundingCycleCalendarPage\"")))
				.andReturn().getResponse().getContentAsString();

		Matcher startDateNoiMatcher = startDateNoiRegex.matcher(response);

		int numMatches = 0;
		while (startDateNoiMatcher.find()) {
			++numMatches;
		}
		
		System.out.println(plusMinusMonth);
		System.out.println(response);

		Assertions.assertEquals(2, numMatches, "At the beginning of every month, we have to adjust the plusMinusMonth request"
				+ " param so that it corresponds to January 2021; there are 2 Funding Cycles for CIHR that have a start"
				+ " date in January 2021.");
	}

	@Tag("user_story_19420")
	@WithAnonymousUser
	@Test
	public void test_anonUserCanAccessViewFcFromFyPage_shouldSucceedWith200() throws Exception {
		Long fyId = 2L;
		
		String response = mvc.perform(MockMvcRequestBuilders.get("/browse/viewFCsForFY").param("fyId", fyId.toString()))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		
		Assertions.assertTrue(response.contains("id=\"viewFundingCyclesForFiscalYearPage\""));
		
		int numRowsExpected = fcService.findFundingCyclesByFiscalYearId(fyId).size();
		int numRowsCounted = 0;
		
		// Each row in the table should contain to a link to the corresponding FO
		Pattern foLinkRegex = Pattern.compile("<td><a href=\"/browse/viewFo\\?id=\\d{1,3}\">");
		Matcher foLinkMatcher = foLinkRegex.matcher(response);
		while (foLinkMatcher.find()) {
			++numRowsCounted;
		}
		
		Assertions.assertEquals(numRowsExpected, numRowsCounted, "There should be " + numRowsExpected + " FundingCycles associated with the 2018 FiscalYear.");
	}

}
