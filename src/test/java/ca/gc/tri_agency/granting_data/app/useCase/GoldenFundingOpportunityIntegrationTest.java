package ca.gc.tri_agency.granting_data.app.useCase;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.controller.FundingOpportunityController;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.repo.SystemFundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class GoldenFundingOpportunityIntegrationTest {

	@Autowired
	private FundingOpportunityController foController;

	@Autowired
	private FundingOpportunityRepository foRepo;

	@Autowired
	SystemFundingOpportunityRepository sfoRepo;

	@Autowired
	private FundingOpportunityService foService;

	@Autowired
	private WebApplicationContext ctx;

	@Mock
	private BindingResult bindingResult;

	@Mock
	private Model model;

	@Mock
	private RedirectAttributes redirectAttributes;

	private MockMvc mvc;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(ctx).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Tag("user_story_14593")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_nameFieldsEmptyOnAddFoPageWhenNewSfoNotLinkedWithSfo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/createFO")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("name=\"nameEn\" value=\"\"")));
	}

	@Tag("user_story_14593")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_nameFieldsAutoFilledOnAddFoPageWhenLinkingNewFoWithSfo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/admin/createFO").param("sfoId", "4")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("value=\"Insight Grants\"")));
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateGoldenFo_shouldFailWith403() throws Exception {
		boolean cpx = true;
		String div = RandomStringUtils.randomAlphabetic(10);
		boolean edi = true;
		String frequency = RandomStringUtils.randomAlphabetic(10);
		String ft = RandomStringUtils.randomAlphabetic(10);
		boolean loi = true;
		boolean noi = true;
		boolean ji = true;
		String nameEn = RandomStringUtils.randomAlphabetic(25);
		String nameFr = RandomStringUtils.randomAlphabetic(25);
		String po = RandomStringUtils.randomAlphabetic(25);

		mvc.perform(MockMvcRequestBuilders.post("/admin/createFO").param("nameEn", nameEn).param("nameFr", nameFr).param("division", div)
				.param("isJointInitiative", Boolean.toString(ji)).param("fundingType", ft).param("partnerOrg", po)
				.param("frequency", frequency).param("isComplex", Boolean.toString(cpx))
				.param("isEdiRequired", Boolean.toString(edi)).param("isNOI", Boolean.toString(noi))
				.param("isLOI", Boolean.toString(loi)).param("businessUnit", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_adminCanCreateFO_SucceedWith302() throws Exception {
		boolean cpx = true;
		boolean edi = true;
		String frequency = RandomStringUtils.randomAlphabetic(10);
		String ft = RandomStringUtils.randomAlphabetic(10);
		boolean loi = true;
		boolean noi = true;
		boolean ji = true;
		String nameEn = RandomStringUtils.randomAlphabetic(25);
		String nameFr = RandomStringUtils.randomAlphabetic(25);
		String po = RandomStringUtils.randomAlphabetic(25);

		List<FundingOpportunity> fos = foRepo.findAll();
		String idParam = String.valueOf(fos.get(fos.size() - 1).getId() + 1L);

		mvc.perform(MockMvcRequestBuilders.post("/admin/createFO").param("id", idParam).param("nameEn", nameEn).param("nameFr", nameFr)
				.param("businessUnit", "1").param("isJointInitiative", Boolean.toString(ji)).param("fundingType", ft)
				.param("partnerOrg", po).param("frequency", frequency).param("isComplex", Boolean.toString(cpx))
				.param("isEdiRequired", Boolean.toString(edi)).param("isNoi", Boolean.toString(noi))
				.param("isLoi", Boolean.toString(loi))).andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/browse/fundingOpportunities"))
				.andExpect(MockMvcResultMatchers.flash().attribute("actionMsg", "Created Funding Opportunity: " + nameEn));

		// when the page is refreshed, the flash attribute should disappear
		mvc.perform(MockMvcRequestBuilders.get("/browse/fundingOpportunities"))
				.andExpect(MockMvcResultMatchers.flash().attributeCount(0));

		fos = foRepo.findAll();
		FundingOpportunity newGfo = fos.get(fos.size() - 1);

		assertEquals(idParam, String.valueOf(newGfo.getId()));
		assertEquals(cpx, newGfo.getIsComplex());
		assertEquals(1L, newGfo.getBusinessUnit().getId());
		assertEquals(edi, newGfo.getIsEdiRequired());
		assertEquals(frequency, newGfo.getFrequency());
		assertEquals(ft, newGfo.getFundingType());
		assertEquals(loi, newGfo.getIsLoi());
		assertEquals(noi, newGfo.getIsNoi());
		assertEquals(ji, newGfo.getIsJointInitiative());
		assertEquals(nameEn, newGfo.getNameEn());
		assertEquals(nameFr, newGfo.getNameFr());
		assertEquals(po, newGfo.getPartnerOrg());
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateGoldenFo_shouldThrowDataAccessException() throws Exception {
		assertThrows(AccessDeniedException.class, () -> foController.createFundingOpportunityPost(new FundingOpportunity(),
				bindingResult, model, redirectAttributes));
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateGoldenFo_shouldThrowAccessDeniedException() {
		assertThrows(AccessDeniedException.class, () -> foService.saveFundingOpportunity(new FundingOpportunity()));
	}

	@Tag("user_story_14593")
	@WithMockUser(roles = "MDM ADMIN")
	@Test
	public void test_createFoFromValidationErrMsgs_shouldReturn200() throws Exception {
		long initFoCount = foRepo.count();

		String response = mvc.perform(MockMvcRequestBuilders.post("/admin/createFO")).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertTrue(response.contains("id=\"createFundingOpportunityPage\""));
		assertTrue(response.contains("The form could not be submitted because 5 errors were found."));

		assertEquals(initFoCount, foRepo.count());
	}

}
