package ca.gc.tri_agency.granting_data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.tri_agency.granting_data.model.Agency;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.model.auditing.UsernameRevisionEntity;
import ca.gc.tri_agency.granting_data.model.projection.FundingOpportunityProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.FundingOpportunityRepository;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.AuditService;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;

@Service
public class FundingOpportunityServiceImpl implements FundingOpportunityService {

	private static final String ENTITY_TYPE = "FundingOpportunity";

	private FundingOpportunityRepository foRepo;

	private AuditService auditService;

	private final String COL_SEPARATOR = "\n~@~\n";

	@Autowired
	public FundingOpportunityServiceImpl(FundingOpportunityRepository foRepo, AuditService auditService) {
		this.foRepo = foRepo;
		this.auditService = auditService;
	}

	@Override
	public FundingOpportunity findFundingOpportunityById(Long foId) {
		return foRepo.findById(foId).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, foId)));
	}

	@Override
	public List<FundingOpportunity> findAllFundingOpportunities() {
		return foRepo.findAll();
	}

	@Override
	public List<FundingOpportunity> findFundingOpportunitiesByNameEn(String nameEn) {
		return foRepo.findByNameEn(nameEn);
	}

	@Override
	public List<FundingOpportunity> findFundingOpportunitiesByBusinessUnitId(Long buId) {
		return foRepo.findByBusinessUnitId(buId);
	}

	@AdminOnly
	@Override
	public FundingOpportunity saveFundingOpportunity(FundingOpportunity fo) {
		return foRepo.save(fo);
	}

	@Override
	public List<FundingOpportunity> findFundingOpportunitiesByAgency(Agency agency) {
		return foRepo.findByAgency(agency);
	}

	@Override
	public List<String[]> findGoldenListTableResults() {
		List<FundingOpportunityProjection> resultSet = foRepo.findResultsForGoldenListTable();

		Map<String, String[]> resultSetMapWithoutValues = new HashMap<>();

		resultSet.forEach(projection -> {
			String key = projection.getId().toString() + COL_SEPARATOR + projection.getNameEn() + COL_SEPARATOR
					+ projection.getNameFr() + COL_SEPARATOR + projection.getBusinessUnitNameEn() + COL_SEPARATOR
					+ projection.getBusinessUnitNameFr();
			resultSetMapWithoutValues.put(key, new String[] { "", "" });
		});

		Map<String, String[]> resultSetMapWithValues = extractApplyAndAwardSystemsForGoldenList(resultSet, resultSetMapWithoutValues);

		return convertResultSetMapForGoldenList(resultSetMapWithValues);
	}

	@Transactional(readOnly = true)
	private List<FundingOpportunityProjection> findGoldenListTableResultsHelper() {
		return foRepo.findResultsForGoldenListTable();
	}

	private Map<String, String[]> extractApplyAndAwardSystemsForGoldenList(List<FundingOpportunityProjection> foProjections,
			Map<String, String[]> resultSetMap) {
		foProjections.forEach(projection -> {

			String grantingSys = projection.getGrantingSystemAcronym();

			resultSetMap.forEach((k, v) -> {
				String[] id = k.split(COL_SEPARATOR);
				if (projection.getGrantingStageId() != null && id[0].equals(projection.getId().toString())) {

					if (projection.getGrantingStageId() == 2L) {
						v[0] = grantingSys;
					} else if (projection.getGrantingStageId() == 4L) {
						v[1] += " / " + grantingSys;
					}

				}
			});

		});

		return resultSetMap;
	}

	private List<String[]> convertResultSetMapForGoldenList(Map<String, String[]> resultSetMap) {
		List<String[]> retVal = new ArrayList<>();

		resultSetMap.forEach((k, v) -> {
			String[] idNameBu = k.split(COL_SEPARATOR);
			retVal.add(new String[] { idNameBu[0], idNameBu[1], idNameBu[2], idNameBu[3], idNameBu[4], v[0],
					v[1].length() > 3 ? v[1].substring(3) : "" });
		});

		return retVal;
	}

	/*
	 * This returns a List b/c a FO can have multiple participating Agencies
	 */
	@Transactional(readOnly = true)
	@Override
	public List<FundingOpportunityProjection> findBrowseViewFoResult(Long foId) throws DataRetrievalFailureException {
		List<FundingOpportunityProjection> foProjections = foRepo.findResultsForViewFO(foId);

		if (foProjections.size() == 0) {
			throw new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, foId));
		}

		return foProjections;
	}

	private List<String[]> convertAuditResults(List<Object[]> revisionList) {
		List<String[]> auditArrList = new ArrayList<>();

		revisionList.forEach(objArr -> {
			FundingOpportunity fo = (FundingOpportunity) objArr[0];
			UsernameRevisionEntity revEntity = (UsernameRevisionEntity) objArr[1];
			RevisionType revType = (RevisionType) objArr[2];

			auditArrList.add(new String[] { fo.getId().toString(), revEntity.getUsername(), revType.toString(), fo.getNameEn(),
					fo.getNameFr(), fo.getFrequency(), fo.getFundingType(),
					(fo.getIsComplex() != null) ? fo.getIsComplex().toString() : null,
					(fo.getIsEdiRequired() != null) ? fo.getIsEdiRequired().toString() : null,
					(fo.getIsJointInitiative() != null) ? fo.getIsJointInitiative().toString() : null,
					(fo.getIsLoi() != null) ? fo.getIsLoi().toString() : null,
					(fo.getIsNoi() != null) ? fo.getIsNoi().toString() : null, fo.getPartnerOrg(),
					(fo.getBusinessUnit() != null) ? fo.getBusinessUnit().getId().toString() : null,
					revEntity.getRevTimestamp().toString()

			});
		});

		return auditArrList;
	}

	@AdminOnly
	@Override
	public List<String[]> findFundingOpportunityRevisionsById(Long foId) {
		return convertAuditResults(auditService.findRevisionsForOneFO(foId));
	}

	@AdminOnly
	@Override
	public List<String[]> findAllFundingOpportunitiesRevisions() {
		return convertAuditResults(auditService.findRevisionsForAllFOs());
	}

	@Override
	public boolean checkIfFundingOpportunityExistsById(Long foId) {
		return foRepo.existsById(foId);
	}

	@Override
	public FundingOpportunityProjection findFundingOpportunityName(Long foId) {
		return foRepo.findName(foId).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, foId)));
	}

	/*
	 * FO has a many-to-many relationship with Agency thus many rows can be returned when eager fetching
	 */
	@Override
	public List<FundingOpportunity> findFundingOpportunityEager(Long foId) {
		List<FundingOpportunity> foList = foRepo.findEager(foId);

		if (foList.isEmpty()) {
			throw new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, foId));
		}

		return foList;
	}

	@Override
	public List<FundingOpportunityProjection> findAllFundingOpportunityNames() {
		return foRepo.findAllNames();
	}

}
