package ca.gc.tri_agency.granting_data.businessunitintegrationtest;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.BusinessUnit;
import ca.gc.tri_agency.granting_data.repo.BusinessUnitRepository;
import ca.gc.tri_agency.granting_data.service.AgencyService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class CreateBusinessUnitIntegrationTest {

	@Autowired
	private BusinessUnitRepository buRepo;
	@Autowired
	private AgencyService agencyService;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Tag("user_story_19048")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_createBULinkVisibleToAdmin_shouldSucceedWith200() throws Exception {
		// CREATE LINK ACCESSIBLE VROM VIEW AGENCY PAGE, ONLY ACCESSIBLE BY ADMIN
		mvc.perform(get("/browse/viewAgency?id=1")).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("id=\"createBusinessUnit\"")));
	}

	@Tag("user_story_19048")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_createBULinkNotVisibleToNonAdmin_shouldReturn200() throws Exception {
		mvc.perform(get("/browse/viewAgency?id=1")).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(not(Matchers.containsString("id=\"createBusinessUnit\""))));
	}

	@Tag("user_story_19048")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanAccessCreateBUPage_shouldSucceedWith200() throws Exception {
		String agencyName = agencyService.findAgencyName(1L).getNameEn();
		assertTrue(mvc.perform(get("/admin/createBU?agencyId=1")).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString().contains('>' + agencyName + "</label>"));
	}

	@Tag("user_story_19048")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdmminCannotAccessCreateBUPage_Return403() throws Exception {
		assertTrue(mvc.perform(get("/admin/createBU?agencyId=1")).andExpect(status().isForbidden()).andReturn().getResponse()
				.getContentAsString().contains("id=\"forbiddenByRoleErrorPage\""));
	}

	@Tag("user_story_19048")
	@WithMockUser(roles = { "MDM ADMIN" })
	@Test
	public void testController_adminCanCreateBU_shouldSucceedWith302() throws Exception {
		long initBuCount = buRepo.count();

		String nameEn = RandomStringUtils.randomAlphabetic(20);
		String nameFr = RandomStringUtils.randomAlphabetic(20);
		String acronymEn = RandomStringUtils.randomAlphabetic(5);
		String acronymFr = RandomStringUtils.randomAlphabetic(5);
		Long agencyId = 1L;

		mvc.perform(MockMvcRequestBuilders.post("/admin/createBU").param("agencyId", Long.toString(agencyId)).param("nameEn", nameEn)
				.param("nameFr", nameFr).param("acronymEn", acronymEn).param("acronymFr", acronymFr)
				.param("agency", Long.toString(agencyId))).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/browse/viewAgency?id=" + Long.toString(agencyId)))
				.andExpect(MockMvcResultMatchers.flash().attribute("actionMsg", "Created Business Unit: " + nameEn));

		// when viewBU page is refreshed, flash attribute should disappear
		mvc.perform(MockMvcRequestBuilders.get("/browse/viewAgency?id=" + Long.toString(agencyId)))
				.andExpect(MockMvcResultMatchers.flash().attributeCount(0));

		assertEquals(initBuCount + 1, buRepo.count());

		BusinessUnit addedBu = buRepo.findAll().get((int) initBuCount);
		assertEquals(nameEn, addedBu.getNameEn());
		assertEquals(nameFr, addedBu.getNameFr());
		assertEquals(acronymEn, addedBu.getAcronymEn());
		assertEquals(acronymFr, addedBu.getAcronymFr());
		assertEquals(agencyId, addedBu.getAgency().getId());
	}

	@Tag("user_story_19048")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void testController_nonAdminCannotCreateBU_shouldReturn403() throws Exception {
		long initBuCount = buRepo.count();

		String nameEn = RandomStringUtils.randomAlphabetic(20);
		String nameFr = RandomStringUtils.randomAlphabetic(20);
		String acronymEn = RandomStringUtils.randomAlphabetic(5);
		String acronymFr = RandomStringUtils.randomAlphabetic(5);
		Long agencyId = 1L;

		assertTrue(mvc.perform(MockMvcRequestBuilders.post("/admin/createBU").param("agencyId", Long.toString(agencyId))
				.param("nameEn", nameEn).param("nameFr", nameFr).param("acronymEn", acronymEn).param("acronymFr", acronymFr)
				.param("agency", Long.toString(agencyId))).andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn()
				.getResponse().getContentAsString().contains("id=\"forbiddenByRoleErrorPage\""));

		assertEquals(initBuCount, buRepo.count());
	}

	@Tag("user_story_19048")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_verfiyFormValidationErrorMsgs_shouldReturn200() throws Exception {
		long initBuCount = buRepo.count();

		// the URL request parameter (agencyId) and the hidden input element (agency) must both be included in the request
		// in order to avoid a 400 response code
		String response = mvc.perform(MockMvcRequestBuilders.post("/admin/createBU").param("agencyId", "2").param("agency", "2")
				.param("nameEn", "").param("nameFr", "").param("acronymEn", "").param("acronymFr", ""))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

		assertTrue(response.contains("id=\"createBusinessUnitPage\""));
		assertTrue(response.contains("The form could not be submitted because 6 errors were found."));

		assertEquals(initBuCount, buRepo.count());
	}

}
