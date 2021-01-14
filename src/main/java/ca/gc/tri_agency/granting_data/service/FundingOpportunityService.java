package ca.gc.tri_agency.granting_data.service;

import java.util.List;

import ca.gc.tri_agency.granting_data.model.Agency;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.model.projection.FundingOpportunityProjection;

public interface FundingOpportunityService {
	
	FundingOpportunity findFundingOpportunityById(Long foId);
	
	List<FundingOpportunity> findAllFundingOpportunities();
	
	List<FundingOpportunity> findFundingOpportunitiesByNameEn(String nameEn);
	
	List<FundingOpportunity> findFundingOpportunitiesByBusinessUnitId(Long buId);
	
	FundingOpportunity saveFundingOpportunity(FundingOpportunity fo);
	
	List<FundingOpportunity> findFundingOpportunitiesByAgency(Agency agency);
	
	List<String[]> findFundingOpportunityRevisionsById(Long foId);

	List<String[]> findAllFundingOpportunitiesRevisions();

	List<String[]> findGoldenListTableResults();
	
	List<FundingOpportunityProjection> findBrowseViewFoResult(Long foId);
	
	boolean checkIfFundingOpportunityExistsById(Long foId);
	
	FundingOpportunityProjection findFundingOpportunityName(Long foId);

	List<FundingOpportunity> findFundingOpportunityEager(Long foId);

	List<FundingOpportunityProjection> findAllFundingOpportunityNames();
}
