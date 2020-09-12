package ca.gc.tri_agency.granting_data.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import ca.gc.tri_agency.granting_data.batch.pojo.FundingCycleExcelRow;
import ca.gc.tri_agency.granting_data.model.SystemFundingCycle;

public class ExcelItemProcessorListener implements ItemProcessListener<FundingCycleExcelRow, SystemFundingCycle> {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelItemProcessorListener.class);

	@Override
	public void beforeProcess(FundingCycleExcelRow item) {
	}

	@Override
	public void afterProcess(FundingCycleExcelRow item, SystemFundingCycle result) {
		if (result == null) {
			LOG.info("A SFO with the ExtID={} does not exist", item.getFoCycle());
			System.out.println();
		}
	}

	@Override
	public void onProcessError(FundingCycleExcelRow item, Exception e) {
	}

}
