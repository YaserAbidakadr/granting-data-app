package ca.gc.tri_agency.granting_data.service;

import java.util.List;
import java.util.Map;

import ca.gc.tri_agency.granting_data.model.FundingCycle;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.model.SystemFundingCycle;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;

public interface DataAccessService {
	List<SystemFundingOpportunity> getAllSystemFOs();

	SystemFundingOpportunity getSystemFO(long id);

	List<FundingOpportunity> getAllFundingOpportunities();

	FundingOpportunity getFundingOpportunity(long id);

	List<SystemFundingCycle> getSystemFundingCyclesByFoId(Long id);

	List<FundingOpportunity> getFoByNameEn(String nameEn);

	Map<Long, FundingCycle> getFundingCycleByFundingOpportunityMap();

	List<FundingOpportunity> getAgencyFundingOpportunities(long id);

	void createFo(FundingOpportunity fo);

}
