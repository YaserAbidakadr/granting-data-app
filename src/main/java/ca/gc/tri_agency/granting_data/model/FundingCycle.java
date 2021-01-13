package ca.gc.tri_agency.granting_data.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import ca.gc.tri_agency.granting_data.model.util.LocalizedParametersModel;

@Entity
public class FundingCycle implements LocalizedParametersModel {

	@Id
	@SequenceGenerator(name = "SEQ_FUNDING_CYCLE", sequenceName = "SEQ_FUNDING_CYCLE", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FUNDING_CYCLE")
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fiscal_year_id")
	private FiscalYear fiscalYear;

	private Boolean isOpen = false;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "start_date_noi")
	private LocalDate startDateNOI;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "start_date_loi")
	private LocalDate startDateLOI;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "end_date_noi")
	private LocalDate endDateNOI;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "end_date_loi")
	private LocalDate endDateLOI;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	@NotNull
	@Min(value = 1, message = "{fc.expectedApplications.min}")
	private Long expectedApplications;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funding_opportunity_id")
	private FundingOpportunity fundingOpportunity;

	/*
	 * could add: private SimpleDateFormat applyDeadlineDate;
	 */

	public FundingCycle() {
	}

	public FundingCycle(FiscalYear fiscalYear, boolean isOpen, LocalDate startDate, LocalDate startDateNOI, LocalDate startDateLOI,
			LocalDate endDateNOI, LocalDate endDateLOI, LocalDate endDate, @Min(1) @NotNull Long expectedApplications,
			FundingOpportunity fundingOpportunity) {
		this.fiscalYear = fiscalYear;
		this.isOpen = isOpen;
		this.startDate = startDate;
		this.startDateNOI = startDateNOI;
		this.startDateLOI = startDateLOI;
		this.endDateNOI = endDateNOI;
		this.endDateLOI = endDateLOI;
		this.endDate = endDate;
		this.expectedApplications = expectedApplications;
		this.fundingOpportunity = fundingOpportunity;
	}

	public Long getId() {
		return id;
	}
	
	public FiscalYear getFiscalYear() {
		return fiscalYear;
	}

	public void setFiscalYear(FiscalYear fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public FundingOpportunity getFundingOpportunity() {
		return fundingOpportunity;
	}

	public void setFundingOpportunity(FundingOpportunity program) {
		this.fundingOpportunity = program;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Long getExpectedApplications() {
		return expectedApplications;
	}

	public void setExpectedApplications(Long expectedApplications) {
		this.expectedApplications = expectedApplications;
	}

	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getStartDateNOI() {
		return startDateNOI;
	}

	public void setStartDateNOI(LocalDate startDateNOI) {
		this.startDateNOI = startDateNOI;
	}

	public LocalDate getStartDateLOI() {
		return startDateLOI;
	}

	public void setStartDateLOI(LocalDate startDateLOI) {
		this.startDateLOI = startDateLOI;
	}

	public LocalDate getEndDateNOI() {
		return endDateNOI;
	}

	public void setEndDateNOI(LocalDate endDateNOI) {
		this.endDateNOI = endDateNOI;
	}

	public LocalDate getEndDateLOI() {
		return endDateLOI;
	}

	public void setEndDateLOI(LocalDate endDateLOI) {
		this.endDateLOI = endDateLOI;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		return id != null && id.equals(((FundingCycle) obj).getId());
	}
	
	@Override
	public int hashCode() {
		return 2020;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FundingCycle [id=");
		builder.append(id);
		builder.append(", isOpen=");
		builder.append(isOpen);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", startDateNOI=");
		builder.append(startDateNOI);
		builder.append(", startDateLOI=");
		builder.append(startDateLOI);
		builder.append(", endDateNOI=");
		builder.append(endDateNOI);
		builder.append(", endDateLOI=");
		builder.append(endDateLOI);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", expectedApplications=");
		builder.append(expectedApplications);
		builder.append("]");
		return builder.toString();
	}

}
