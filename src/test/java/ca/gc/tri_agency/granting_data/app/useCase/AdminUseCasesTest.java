package ca.gc.tri_agency.granting_data.app.useCase;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class AdminUseCasesTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Autowired
	private FundingOpportunityRepository foRepo;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Tag("user_story_14589")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_requestSelectBackendFileForComparison_withAdminUser_shouldSucceedWith200() throws Exception {
		mvc.perform(get("/admin/selectFileForComparison").contentType(MediaType.APPLICATION_XHTML_XML))
				.andExpect(status().isOk());
	}
	
	@Tag("user_story_14589")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_selectFileForCopmarisonFilePageLinkRequests() throws Exception {
		mvc.perform(get("/admin/analyzeFoUploadData").param("filename", "CRM-TestFile.xlsx"))
				.andExpect(status().isOk());
	}

	@Tag("user_story_14627")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_databaseIsPopulatedOnInstall() {
		assertTrue(foRepo.findAll().size() > 100, "There are " + foRepo.findAll().size() + " FOs in the db");
	}

}
