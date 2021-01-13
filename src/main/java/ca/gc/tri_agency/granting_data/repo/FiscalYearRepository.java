package ca.gc.tri_agency.granting_data.repo;

import java.util.List;
import java.util.Optional;

import javax.persistence.OrderBy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.tri_agency.granting_data.model.FiscalYear;
import ca.gc.tri_agency.granting_data.model.projection.FiscalYearProjection;

@Repository
@Transactional(readOnly = true)
public interface FiscalYearRepository extends JpaRepository<FiscalYear, Long>, QueryByExampleExecutor<FiscalYear> { // @formatter:off

	Optional<FiscalYear> findByYear(Long year);

	/*
	 * This query sums both the FCs and SFCs for each FY
	 * 
	 * @Query(value = "SELECT FY.ID AS id, FY.YEAR AS year,"
			+ " ISNULL(SUM(FC.EXPECTED_APPLICATIONS), 0) + ISNULL(SUM(SFC.NUM_APPS_RECEIVED), 0) AS numAppsReceived"
			+ " FROM FISCAL_YEAR FY"
			+ " LEFT JOIN FUNDING_CYCLE FC ON FY.ID = FC.FISCAL_YEAR_ID"
			+ " LEFT JOIN SYSTEM_FUNDING_CYCLE SFC ON FY.YEAR = SFC.FISCAL_YEAR"
			+ " GROUP BY FY.ID, FY.YEAR"
			+ " ORDER BY FY.YEAR;", nativeQuery = true)
	 */
	@Query("SELECT fy.id AS id, fy.year AS year, SUM(fc.expectedApplications) AS numAppsReceived"
			+ " FROM FiscalYear fy"
			+ " LEFT JOIN FundingCycle fc ON fy.id = fc.fiscalYear.id"
			+ " GROUP BY fy.year, fy.id"
			+ " ORDER BY fy.year")
	List<FiscalYearProjection> findNumAppsExpectedForEachYear();

	@Query("SELECT id AS id, year AS year FROM FiscalYear ORDER BY year")
	List<FiscalYearProjection> findAllProjectionsOrderByYear();

	@OrderBy("year")
	List<FiscalYear> findAll();

} // @formatter:on
