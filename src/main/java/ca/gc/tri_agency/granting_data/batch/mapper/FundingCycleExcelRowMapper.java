package ca.gc.tri_agency.granting_data.batch.mapper;

import org.springframework.batch.item.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.validation.BindException;

import ca.gc.tri_agency.granting_data.batch.pojo.FundingCycleExcelRow;

public class FundingCycleExcelRowMapper extends BeanWrapperRowMapper<FundingCycleExcelRow> {

	@Override
	public FundingCycleExcelRow mapRow(RowSet rs) throws BindException {
		FundingCycleExcelRow fc = new FundingCycleExcelRow();
		
		fc.setFoCycle(rs.getColumnValue(0));
		fc.setCompetitionYear((long) Double.parseDouble(rs.getColumnValue(1)));
		fc.setProgramId(rs.getColumnValue(2));
		fc.setProgramNameEn(rs.getColumnValue(3));
		fc.setProgramNameFr(rs.getColumnValue(4));
		fc.setNumReceivedApps((long) Double.parseDouble(rs.getColumnValue(5)));
		
		return fc;
	}

}
