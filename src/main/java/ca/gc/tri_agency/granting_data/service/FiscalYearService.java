package ca.gc.tri_agency.granting_data.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;

import ca.gc.tri_agency.granting_data.model.FiscalYear;
import ca.gc.tri_agency.granting_data.model.projection.FiscalYearProjection;

public interface FiscalYearService {

	FiscalYear findFiscalYearById(Long id);

	List<FiscalYearProjection> findAllFiscalYearProjectionsOrderByYear();

	Optional<FiscalYear> findFiscalYearByYear(Long yr);

	FiscalYear saveFiscalYear(FiscalYear fy) throws DataIntegrityViolationException;

	List<FiscalYearProjection> findNumAppsExpectedForEachFiscalYear();

	List<FiscalYear> findAllFiscalYearEntitiesOrderByYear();

	boolean checkIfFiscalYearExists(FiscalYear fy);
}
