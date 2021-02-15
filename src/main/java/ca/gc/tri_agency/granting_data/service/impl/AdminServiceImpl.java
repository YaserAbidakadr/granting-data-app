package ca.gc.tri_agency.granting_data.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ebay.xcelite.Xcelite;
import com.ebay.xcelite.reader.SheetReader;
import com.ebay.xcelite.sheet.XceliteSheet;

import ca.gc.tri_agency.granting_data.model.GrantingSystem;
import ca.gc.tri_agency.granting_data.model.SystemFundingCycle;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.model.file.FundingCycleDatasetRow;
import ca.gc.tri_agency.granting_data.model.projection.SystemFundingCycleProjection;
import ca.gc.tri_agency.granting_data.service.AdminService;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;
import ca.gc.tri_agency.granting_data.service.SystemFundingCycleService;
import ca.gc.tri_agency.granting_data.service.SystemFundingOpportunityService;

@Service
public class AdminServiceImpl implements AdminService {

	private SystemFundingCycleService sfcService;

	private SystemFundingOpportunityService sfoService;

	private GrantingSystemService gsService;

	@Value("${dataset.analysis.folder}")
	private String datasetAnalysisFolder;
	
	private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

	@Autowired
	public AdminServiceImpl(SystemFundingCycleService sfcService, SystemFundingOpportunityService sfoService,
			GrantingSystemService gsService) {
		this.sfcService = sfcService;
		this.sfoService = sfoService;
		this.gsService = gsService;
	}

	@Override
	public List<File> getDatasetFiles() {
		Path dir = Paths.get(datasetAnalysisFolder);
		List<File> list = new ArrayList<File>();
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
			dirStream.forEach(path -> list.add(path.toFile()));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return list;
	}

	@Override
	public List<FundingCycleDatasetRow> getFundingCyclesFromFile(String filename) {
		Collection<FundingCycleDatasetRow> rows = null;
		Xcelite xcelite;
		xcelite = new Xcelite(new File(datasetAnalysisFolder + filename));
		XceliteSheet sheet = xcelite.getSheet(0);
		SheetReader<FundingCycleDatasetRow> reader = sheet.getBeanReader(FundingCycleDatasetRow.class);
		try {
			rows = reader.read();
		} catch (NoSuchElementException nse) {
			LOG.error("That file contains no FundingCycleDataSetRows");
			return new ArrayList<FundingCycleDatasetRow>();
		}

		return new ArrayList<FundingCycleDatasetRow>(rows);
	}

	@Override
	public List<String> generateActionableFoCycleIds(List<FundingCycleDatasetRow> foCycles) {
		// SYSTEM FCs HAVE UNIQUE IDENTIFIER THAT INCLUDES THE PROGRAM IDENTIFIER. USING
		// THAT AS DETERMINATION FACTOR
		List<SystemFundingCycleProjection> dbFundingCycles = sfcService.findAllSystemFundingCycleExtIds();
		List<String> retval = new ArrayList<String>();
		for (FundingCycleDatasetRow row : foCycles) {
			boolean rowFound = false;
			for (SystemFundingCycleProjection cycle : dbFundingCycles) {
				if (cycle.getExtId().compareTo(row.getFoCycle()) == 0) {
					rowFound = true;
				}
			}
			if (rowFound == false) {
				retval.add(row.getFoCycle());
			}
		}
		return retval;
	}

	@Override
	public int applyChangesFromFileByIds(String filename, String[] idsToAction) {
		GrantingSystem targetSystem = gsService.findGrantingSystemFromFile(filename);

		// transform into list for inspection capabilities
		List<String> actionList = Arrays.asList(idsToAction);

		// generate map for lookup
		List<SystemFundingOpportunity> foList = sfoService.findAllSystemFundingOpportunities();
		HashMap<String, SystemFundingOpportunity> map = new HashMap<String, SystemFundingOpportunity>();
		for (SystemFundingOpportunity fo : foList) {
			map.put(fo.getExtId(), fo);
		}
		// Set<String> foIdList = map.keySet();
		List<FundingCycleDatasetRow> foCyclesList = getFundingCyclesFromFile(filename);
		List<Long> newIdList = new ArrayList<Long>();
		for (FundingCycleDatasetRow row : foCyclesList) {
			if (actionList.contains(row.getFoCycle())) {
				SystemFundingOpportunity targetFo = null;
				// if program exists, use it, else create it
				// String targetId = row.getProgram_ID().substring(0,
				// row.getProgram_ID().length() - 5);
				if (map.containsKey(row.getProgram_ID())) {
					targetFo = map.get(row.getProgram_ID());
				} else {
					targetFo = sfoService.registerSystemFundingOpportunity(row, targetSystem);
					map.put(row.getProgram_ID(), targetFo);

				}
				SystemFundingCycle newCycle = sfcService.registerSystemFundingCycle(row, targetFo);
				newIdList.add(newCycle.getId());
			}

		}
		return newIdList.size();

	}

}
