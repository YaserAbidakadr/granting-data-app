package ca.gc.tri_agency.granting_data.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.GrantingSystem;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.model.file.FundingCycleDatasetRow;
import ca.gc.tri_agency.granting_data.repo.SystemFundingCycleRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class AdminServiceIntegrationTest {

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private GrantingSystemService gsSystem;
	
	@Autowired
	private SystemFundingCycleService sfcService;

	@Autowired
	private SystemFundingCycleRepository sfcRepo;
	
	@Autowired
	private SystemFundingOpportunityService sfoService;
	
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	private static String testFoName = "TESTFO";

	private static final String TEST_FILE = "CRM-TestFile.xlsx";

	/* TEST UTIL FUNCTION */
	FundingCycleDatasetRow createFcDatasetRow(String foName, String year) {
		FundingCycleDatasetRow retval = new FundingCycleDatasetRow();
		retval.setCompetitionYear(2019L);
		retval.setFoCycle(foName + "-" + year);
		retval.setProgram_ID(foName);
		retval.setProgramNameEn(foName + "-en");
		retval.setProgramNameFr(foName + "-fr");
		retval.setNumReceivedApps(100);
		return retval;
	}

	@Test
	@WithAnonymousUser
	/*
	 * Changed this method because the application currently has the security setup
	 * so that when an unauthorized user tries to access any page that requires
	 * authentication, that unauthorized user is redirected to the login page.
	 */
	public void test_applyChangesFromFileByIds_unauthorizedUserShouldRedirectToLoginPage() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("fileName", TEST_FILE);
		String idToAction = adminService.getFundingCyclesFromFile(TEST_FILE).get(0).getFoCycle();
		paramMap.add("idToAction", idToAction);
		mvc.perform(post("/admin/analyzeFoUploadData").queryParams(paramMap)).andExpect(status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
	}

	@Test
	@WithMockUser(username = "mock_admin", roles = "MDM ADMIN")
	@Transactional
	public void test_applyChangesFromFileByIds_regsiterSFCwhenSFOalreadyExists() {
		String targetYear = "2009";
		String sfoName = "SAMPLE";

		// create new test SFO with no SFCs
		SystemFundingOpportunity newSfo = new SystemFundingOpportunity();
		newSfo.setNameEn(sfoName + " EN");
		newSfo.setNameFr(sfoName + " FR");
		newSfo.setExtId(sfoName);

		// List<GrantingSystem> gsList = gsRepo.findAll();
		newSfo.setGrantingSystem(gsSystem.findGrantingSystemByAcronym("NAMIS"));
		newSfo = sfoService.saveSystemFundingOpportunity(newSfo);
		List<SystemFundingOpportunity> sfoMatchingNames = sfoService.findSystemFundingOpportunitiesByNameEn(sfoName + " EN");
		assertTrue("i think the process and test requires unique SFO names", sfoMatchingNames.size() == 1);

		int origCountOfSFCsLinkedToSFO = sfcRepo.findBySystemFundingOpportunityId(newSfo.getId()).size();
		assertTrue("logic says new SFO shoudl ahve no SFCs", origCountOfSFCsLinkedToSFO == 0);

		// ADD A CYCLE TO "SAMPLE" thought the service
		final String testFileName = "NAMIS-TestCase_registerSFCwhenSFOalreadyExists.xlsx";
		adminService.applyChangesFromFileByIds(testFileName, new String[] { sfoName + "-" + targetYear });

		int newCountOfSFCsLinkedToSFO = sfcRepo.findBySystemFundingOpportunityId(newSfo.getId()).size();

		assertTrue("new count of SFCs linked to SFO should now be 1", newCountOfSFCsLinkedToSFO == 1);
	}

	@Test
	@WithMockUser(username = "mock_admin", roles = "MDM ADMIN")
	public void test_applyChangesFromFileByIds_adminCanRegisterSFOandSFC() throws Exception {
		List<FundingCycleDatasetRow> fundingCycles = adminService.getFundingCyclesFromFile(TEST_FILE);
		String[] idsToAction = new String[] { fundingCycles.get(0).getFoCycle() };
		assertEquals(1, adminService.applyChangesFromFileByIds(TEST_FILE, idsToAction));
	}

	@Test
	public void test_getDatasetFiles_expectNonEmptyList() {
		List<File> myFiles = adminService.getDatasetFiles();
		assertNotEquals(myFiles.size(), 0, "expected non empty list from dataset folder");

	}

	@Test
	public void test_getFundingCyclesFromFile_expect24Rows() {
		List<FundingCycleDatasetRow> rows = adminService.getFundingCyclesFromFile("NAMIS-TestFileBase1.xlsx");
		assertEquals(rows.size(), 24, "expected 24 rows from TestFileBase1.xlsx");
	}

	@Test
	public void test_generateActionableFoCycleIds_actionDueToNewSfoAndSfc() {
		List<FundingCycleDatasetRow> analyzeRows = new ArrayList<FundingCycleDatasetRow>();
		analyzeRows.add(createFcDatasetRow(testFoName + "1", "2019"));
		List<String> result = adminService.generateActionableFoCycleIds(analyzeRows);
		assertEquals(1, result.size(), "expected one action row due to SFC and SFO not created");

	}

	@WithMockUser(username = "mock_admin", roles = { "MDM ADMIN" })
	@Test
	public void test_generateActionableFoCycleIds_actionDueToNewSfc() {
		// register FO
		GrantingSystem gs = gsSystem.findAllGrantingSystems().get(0);
		FundingCycleDatasetRow testRow = createFcDatasetRow(testFoName + "2", "2019");
		sfoService.registerSystemFundingOpportunity(testRow, gs);

		// test
		List<FundingCycleDatasetRow> analyzeRows = new ArrayList<FundingCycleDatasetRow>();
		analyzeRows.add(testRow);
		List<String> result = adminService.generateActionableFoCycleIds(analyzeRows);
		assertEquals(1, result.size(), "expected one action row due to new SFC");

	}

	@WithMockUser(username = "mock_admin", roles = { "MDM ADMIN" })
	@Test
	public void test_generateActionableFoCycleIds_noActionCauseSfcAndSfoExists() {
		GrantingSystem gs = gsSystem.findAllGrantingSystems().get(0);
		FundingCycleDatasetRow testRow = createFcDatasetRow(testFoName + "3", "2019");
		SystemFundingOpportunity sfo = sfoService.registerSystemFundingOpportunity(testRow, gs);
		sfcService.registerSystemFundingCycle(testRow, sfo);

		List<FundingCycleDatasetRow> analyzeRows = new ArrayList<FundingCycleDatasetRow>();
		analyzeRows.add(createFcDatasetRow(testFoName + "3", "2019"));
		List<String> result = adminService.generateActionableFoCycleIds(analyzeRows);
		assertEquals(0, result.size(), "expected zero action rows as data exists");

	}

}
