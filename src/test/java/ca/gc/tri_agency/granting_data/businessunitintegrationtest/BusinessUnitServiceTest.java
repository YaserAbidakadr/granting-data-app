package ca.gc.tri_agency.granting_data.businessunitintegrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.model.Agency;
import ca.gc.tri_agency.granting_data.model.BusinessUnit;
import ca.gc.tri_agency.granting_data.model.projection.BusinessUnitProjection;
import ca.gc.tri_agency.granting_data.repo.BusinessUnitRepository;
import ca.gc.tri_agency.granting_data.service.AgencyService;
import ca.gc.tri_agency.granting_data.service.BusinessUnitService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class BusinessUnitServiceTest {

	@Autowired
	private BusinessUnitService buService;

	@Autowired
	private AgencyService agencyService;

	@Autowired
	private BusinessUnitRepository buRepo;

	@Tag("user_story_19048")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanCreateBU() {
		long initBuCount = buRepo.count();

		Agency agency = agencyService.findAllAgencies().get(0);
		BusinessUnit bu = new BusinessUnit("EN NAME TEST", "FR NAME TEST", "EN ACRONYM TEST", "FR ACRONYM TEST", "TEST@EMAIL.COM",
				agency);
		buService.saveBusinessUnit(bu);

		assertEquals(initBuCount + 1, buRepo.count());
	}

	@Tag("user_story_19048")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotCreateBU_shouldthrowAccessDeniedException() {
		BusinessUnit bu = new BusinessUnit("EN NAME TEST", "FR NAME TEST", "EN ACRONYM TEST", "FR ACRONYM TEST", "TEST@EMAIL.COM",
				agencyService.findAgencyById(1L));
		assertThrows(AccessDeniedException.class, () -> buService.saveBusinessUnit(bu));
	}
	
	@Tag("user_story_19389")
	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void test_adminCanEditBU() {
		long initBuRepoCount = buRepo.count();
		BusinessUnit bu = buRepo.findById(1L).get();

		String nameBefore = bu.getNameEn();

		bu.setNameEn(RandomStringUtils.randomAlphabetic(20));
		bu = buService.saveBusinessUnit(bu);

		assertEquals(initBuRepoCount, buRepo.count());
		assertNotEquals(nameBefore, bu.getNameEn());
	}

	@Tag("user_story_19389")
	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotEditBU() {
		BusinessUnit bu = buRepo.findById(1L).get();
		bu.setNameEn(RandomStringUtils.randomAlphabetic(20));
		bu.setNameFr(RandomStringUtils.randomAlphabetic(20));
		bu.setAcronymEn(RandomStringUtils.randomAlphabetic(5));
		bu.setAcronymFr(RandomStringUtils.randomAlphabetic(5));

		assertThrows(AccessDeniedException.class, () -> buService.saveBusinessUnit(bu));
	}

	@Tag("user_story_19401")
	@WithAnonymousUser
	@Test
	public void test_findAllBUs() {
		assertTrue(14 <= buService.findAllBusinessUnits().size());
	}

	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindBusinessUnitRevisionsById() {
		final Long buId = 1L;
		int startNumRevisions = buService.findBusinessUnitRevisionsById(buId).size();

		BusinessUnit bu = buService.findBusinessUnitById(buId);
		String nameEn = RandomStringUtils.randomAlphabetic(5);
		bu.setNameEn(nameEn);
		buService.saveBusinessUnit(bu);

		List<String[]> buRevisions = buService.findBusinessUnitRevisionsById(buId);
		int endNumRevisions = buRevisions.size();

		assertEquals(startNumRevisions + 1, endNumRevisions);
		assertEquals(nameEn, buRevisions.get(endNumRevisions - 1)[3]);

		int numAdds = 0;

		for (String[] strArr : buRevisions) {
			if (strArr[2].equals("ADD")) {
				++numAdds;
			}
		}

		assertEquals(numAdds, 1);
	}

	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotFindBusinessUnitRevisionsById() {
		assertThrows(AccessDeniedException.class, () -> buService.findBusinessUnitRevisionsById(1L));
	}

	@Tag("user_story_19401")
	@WithAnonymousUser
	@Test
	public void test_findBusinessUnitById_shouldThrowException() {
		assertThrows(DataRetrievalFailureException.class, () -> buService.findBusinessUnitById(Long.MAX_VALUE));
	}

	@WithMockUser(username = "admin", roles = "MDM ADMIN")
	@Test
	public void test_adminCanFindAllBusinessUnitRevisions() {
		List<String[]> auditedArrList = buService.findAllBusinessUnitRevisions();

		int numAdds = 0;

		for (String[] strArr : auditedArrList) {
			if (strArr[2].equals("ADD")) {
				++numAdds;
			}
		}

		assertTrue(numAdds >= 14);
	}

	@WithMockUser(roles = { "NSERC_USER", "SSHRC_USER", "AGENCY_USER" })
	@Test
	public void test_nonAdminCannotFindAllBusinessUnitRevisions() {
		assertThrows(AccessDeniedException.class, () -> buService.findAllBusinessUnitRevisions());
	}

	@Tag("user_story_19147")
	@WithMockUser(username = "aha")
	@Test
	public void test_findEdiAppPartDataForAuthorizedBUMember() {
		Map<String, Long> ediMap = buService.findEdiAppPartDataForAuthorizedBUMember(13L);

		assertEquals(3L, ediMap.get("numIndigenousApps"));
		assertEquals(7L, ediMap.get("numVisMinorityApps"));
		assertEquals(1L, ediMap.get("numDisabledApps"));
		assertEquals(6L, ediMap.get("numFemaleApps"));
		assertEquals(3L, ediMap.get("numMaleApps"));
		assertEquals(2L, ediMap.get("numNonBinaryApps"));
		assertEquals(11L, ediMap.get("numApps"));

		// user "aha" is not authorized to get EDI data associated with BU 1
		assertThrows(AccessDeniedException.class, () -> buService.findEdiAppPartDataForAuthorizedBUMember(1L));
	}

	@Tag("user_story_19401")
	@WithAnonymousUser
	@Test
	public void test_findReultsForBrowseViewBU() {
		List<BusinessUnitProjection> buProjections = buService.findResultsForBrowseViewBU(13L);

		List<Long> mrIds = new ArrayList<>();

		buProjections.forEach(bu -> {
			assertEquals(13L, bu.getId());
			assertEquals(1L, bu.getAgencyId());
			mrIds.add(bu.getMemRoleId());
		});

		assertEquals(mrIds.size(), mrIds.stream().distinct().count());

		assertThrows(DataRetrievalFailureException.class, () -> buService.findResultsForBrowseViewBU(Long.MAX_VALUE));
	}

}
