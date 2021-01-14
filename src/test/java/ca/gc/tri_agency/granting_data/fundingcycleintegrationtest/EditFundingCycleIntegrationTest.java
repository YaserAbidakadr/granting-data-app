package ca.gc.tri_agency.granting_data.fundingcycleintegrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.apache.commons.lang3.RandomUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.FundingCycle;
import ca.gc.tri_agency.granting_data.repo.FundingCycleRepository;
import ca.gc.tri_agency.granting_data.service.FundingCycleService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class EditFundingCycleIntegrationTest {

	@Autowired
	private FundingCycleService fcService;

	@Autowired
	private FundingCycleRepository fcRepo;

	@Autowired
	private WebApplicationContext ctx;

	private static final String EDIT_FC_BTN = "href=\"/manage/editFC?id=";

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_19207")
	@WithMockUser(username = "jfs")
	@Transactional
	@Rollback
	@Test
	public void test_buProgramLeadCanEditFC_shouldSucceedWith302() throws Exception {
		Long fcCount = fcRepo.count();
		Long newNumAppsReceived = RandomUtils.nextLong(1L, 10_000L);
		LocalDate newStartDate = LocalDate.now();

		String fcIdStr = "77";
		Long fcId = 77L;

		FundingCycle fc = fcService.findFundingCycleById(fcId);
		LocalDate initEndDate = fc.getEndDate();
		LocalDate initStartDateNOI = fc.getStartDateNOI();
		LocalDate initEndDateNOI = fc.getEndDateNOI();
		LocalDate initStartDateLOI = fc.getStartDateLOI();
		LocalDate initEndDateLOI = fc.getEndDateLOI();
		Long initNumAppsExpected = fc.getExpectedApplications();
		Long initLinkedFO = fc.getFundingOpportunity().getId();
		Long initFiscalYear = fc.getFiscalYear().getId();
		Boolean initIsOpenEnded = fc.getIsOpen();

		// verify "Edit" button is visible on manage/manageFO
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", fcIdStr)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(EDIT_FC_BTN + fcIdStr)));

		// verify that fields on manage/editFC are populated
		mvc.perform(MockMvcRequestBuilders.get("/manage/editFC").param("id", fcIdStr)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.string(Matchers.containsString("<option value=\"3\" selected=\"selected\">2019</option>")))
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(initNumAppsExpected.toString())));

		// verify that FC is edited
		assertTrue(mvc.perform(MockMvcRequestBuilders.post("/manage/editFC").param("id", fcIdStr)
				.param("fiscalYear", initFiscalYear.toString()).param("startDate", newStartDate.toString())
				.param("isOpen", initIsOpenEnded.toString()).param("startDateNOI", initStartDateNOI.toString())
				.param("startDateLOI", initStartDateLOI.toString()).param("endDate", initEndDate.toString())
				.param("endDateNOI", initEndDateNOI.toString()).param("endDateLOI", initEndDateLOI.toString())
				.param("fundingOpportunity", initLinkedFO.toString())
				.param("expectedApplications", newNumAppsReceived.toString()))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/manage/manageFo?id=" + initLinkedFO.toString())).andReturn()
				.getFlashMap().containsValue("Successfully Edited Funding Cycle"), "Edit success flash attribute is missing");

		FundingCycle editedFC = fcService.findFundingCycleById(fcId);

		assertEquals(fcCount, fcRepo.count());
		assertEquals(newNumAppsReceived, editedFC.getExpectedApplications());
		assertEquals(newStartDate, editedFC.getStartDate());
		assertEquals(initStartDateNOI, editedFC.getStartDateNOI());
		assertEquals(initStartDateLOI, editedFC.getStartDateLOI());
		assertEquals(initEndDate, editedFC.getEndDate());
		assertEquals(initEndDateNOI, editedFC.getEndDateNOI());
		assertEquals(initEndDateLOI, editedFC.getEndDateLOI());
		assertEquals(initIsOpenEnded, editedFC.getIsOpen());
		assertEquals(initFiscalYear, editedFC.getFiscalYear().getId());
		assertEquals(initLinkedFO, editedFC.getFundingOpportunity().getId());
	}

	@Tag("user_story_19207")
	@WithMockUser(username = "jfs")
	@Test
	public void test_buProgramOfficerCannotEditFC_shouldReturn403() throws Exception {
		String fcIdStr = "17";

		Long newNumAppsReceived = RandomUtils.nextLong(1L, 100_000L);

		// verify "Edit" button does not appear for a Program Officer
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "17")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(EDIT_FC_BTN + fcIdStr))));

		// verify Program Officer cannot access manage/editFC page
		mvc.perform(MockMvcRequestBuilders.get("/manage/editFC").param("id", fcIdStr))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// verify Program Officer cannot edit a FC
		mvc.perform(MockMvcRequestBuilders.post("/manage/editFC").param("id", fcIdStr).param("fiscalYear", "3")
				.param("startDate", "2020-01-01").param("isOpen", "true").param("fundingOpportunity", "17")
				.param("expectedApplications", newNumAppsReceived.toString()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		assertNotEquals(newNumAppsReceived, fcService.findFundingCycleById(17L).getExpectedApplications());
	}

	@Tag("user_story_19207")
	@WithMockUser(username = "jfs")
	@Test
	public void test_buNonMemberCannotEditFC_shouldReturn403() throws Exception {
		String fcIdStr = "43";
		Long fcId = 43L;

		Long newNumAppsReceived = RandomUtils.nextLong(1L, 100_000L);

		// verify "Edit" button does not appear for a user that is not a member of the linked FO's BU
		mvc.perform(MockMvcRequestBuilders.get("/manage/manageFo").param("id", "43")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(EDIT_FC_BTN + fcIdStr))));

		// verify that a user who is not a member of the linked FO's BU cannot access manage/editFC page
		mvc.perform(MockMvcRequestBuilders.get("/manage/editFC").param("id", fcIdStr))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		// verify that a user who is not a member of the linked FO's BU cannot edit a FC
		mvc.perform(MockMvcRequestBuilders.post("/manage/editFC").param("id", fcIdStr).param("fiscalYear", "1")
				.param("startDate", "2020-01-01").param("isOpen", "true").param("fundingOpportunity", "43")
				.param("expectedApplications", newNumAppsReceived.toString()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

		assertNotEquals(newNumAppsReceived, fcService.findFundingCycleById(fcId).getExpectedApplications());
	}

	@Tag("user_story_19207")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_verifyEditFcFormValidationErrMsgs_shouldReturn200() throws Exception {
		final long fcId = 1L;
		FundingCycle fcBefore = fcService.findFundingCycleById(fcId);

		// fundingOpportunity is a hidden input
		String response = mvc
				.perform(MockMvcRequestBuilders.post("/manage/editFC").param("id", String.valueOf(fcId))
						.param("fundingOpportunity", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		FundingCycle fcAfter = fcService.findFundingCycleById(fcId);
		
		System.out.println(response);

		assertTrue(response.contains("id=\"editFundingCyclePage\""));
		assertTrue(response.contains("The form could not be submitted because 3 errors were found."));

		assertEquals(fcBefore.getFiscalYear().getId(), fcAfter.getFiscalYear().getId());
		assertEquals(fcBefore.getStartDate(), fcAfter.getStartDate());
		assertEquals(fcBefore.getExpectedApplications(), fcAfter.getExpectedApplications());
		assertEquals(fcBefore.getFundingOpportunity().getId(), fcAfter.getFundingOpportunity().getId());
	}

}
