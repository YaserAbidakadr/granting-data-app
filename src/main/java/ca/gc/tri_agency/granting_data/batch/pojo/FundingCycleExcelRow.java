package ca.gc.tri_agency.granting_data.batch.pojo;

public class FundingCycleExcelRow {

	private String programId;

	private String programNameEn;

	private String programNameFr;

	private Long competitionYear;

	private String foCycle;

	private Long numReceivedApps;
	
	private Boolean exists;

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramNameEn() {
		return programNameEn;
	}

	public void setProgramNameEn(String programNameEn) {
		this.programNameEn = programNameEn;
	}

	public String getProgramNameFr() {
		return programNameFr;
	}

	public void setProgramNameFr(String programNameFr) {
		this.programNameFr = programNameFr;
	}

	public String getFoCycle() {
		return foCycle;
	}

	public void setFoCycle(String foCycle) {
		this.foCycle = foCycle;
	}

	public Long getCompetitionYear() {
		return competitionYear;
	}

	public void setCompetitionYear(Long competitionYear) {
		this.competitionYear = competitionYear;
	}

	public Long getNumReceivedApps() {
		return numReceivedApps;
	}

	public void setNumReceivedApps(Long numReceivedApps) {
		this.numReceivedApps = numReceivedApps;
	}

	public Boolean getExists() {
		return exists;
	}

	public void setExists(Boolean exists) {
		this.exists = exists;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FundingCycleExcelRow [Program_ID=");
		builder.append(programId);
		builder.append(", programNameEn=");
		builder.append(programNameEn);
		builder.append(", programNameFr=");
		builder.append(programNameFr);
		builder.append(", competitionYear=");
		builder.append(competitionYear);
		builder.append(", foCycle=");
		builder.append(foCycle);
		builder.append(", numReceivedApps=");
		builder.append(numReceivedApps);
		builder.append(", exists=");
		builder.append(exists);
		builder.append("]");
		return builder.toString();
	}
	
}
