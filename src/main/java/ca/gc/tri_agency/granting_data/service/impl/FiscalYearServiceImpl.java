package ca.gc.tri_agency.granting_data.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import ca.gc.tri_agency.granting_data.model.FiscalYear;
import ca.gc.tri_agency.granting_data.model.projection.FiscalYearProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.FiscalYearRepository;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.FiscalYearService;

@Service
public class FiscalYearServiceImpl implements FiscalYearService {
	
	private static final String ENTITY_TYPE = "FiscalYear";

	private FiscalYearRepository fyRepo;

	@Autowired
	public FiscalYearServiceImpl(FiscalYearRepository fyRepo) {
		this.fyRepo = fyRepo;
	}

	@Override
	public FiscalYear findFiscalYearById(Long id) {
		return fyRepo.findById(id).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, id)));
	}

	@Override
	public List<FiscalYearProjection> findAllFiscalYearProjectionsOrderByYear() {
		return fyRepo.findAllProjectionsOrderByYear();
	}

	@Override
	public Optional<FiscalYear> findFiscalYearByYear(Long yr) {
		return fyRepo.findByYear(yr);
	}

	@AdminOnly
	@Override
	public FiscalYear saveFiscalYear(FiscalYear fy) throws DataIntegrityViolationException {
		return fyRepo.save(fy);
	}

	@Override
	public List<FiscalYearProjection> findNumAppsExpectedForEachFiscalYear() {
		return fyRepo.findNumAppsExpectedForEachYear();
	}
	
	@Override
	public List<FiscalYear> findAllFiscalYearEntitiesOrderByYear() {
		return fyRepo.findAll();
	}

	@Override
	public boolean checkIfFiscalYearExists(FiscalYear fy) {
		if (fy.getYear() == null) {
			return false;
		}
		
		Example<FiscalYear> fyExample = Example.of(fy);
		return fyRepo.exists(fyExample);
	}
}
