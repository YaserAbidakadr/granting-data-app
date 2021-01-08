package ca.gc.tri_agency.granting_data.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.ebay.xcelite.annotations.Column;

import ca.gc.tri_agency.granting_data.model.util.LocalizedParametersModel;

@Entity
@Audited
public class FundingOpportunity implements LocalizedParametersModel {

	@Id
	@SequenceGenerator(name = "SEQ_FUNDING_OPPORTUNITY", sequenceName = "SEQ_FUNDING_OPPORTUNITY", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FUNDING_OPPORTUNITY")
	private Long id;

	@NotBlank
	private String nameEn;

	@NotBlank
	private String nameFr;

	@NotBlank
	private String fundingType; // could be dropped

	@NotBlank
	private String frequency;

	private Boolean isJointInitiative = false;

	@Column(name = "is_noi")
	private Boolean isNoi = false;

	@Column(name = "is_loi")
	private Boolean isLoi = false;

	private String partnerOrg;

	private Boolean isEdiRequired = false;

	private Boolean isComplex = false;

	@NotNull
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "business_unit_id")
	private BusinessUnit businessUnit;
	
	@NotAudited
	@ManyToMany
	@JoinTable(name = "FUNDING_OPPORTUNITY_PARTICIPATING_AGENCIES", 
		joinColumns = @JoinColumn(name = "funding_opportunity_id"), 
		inverseJoinColumns = @JoinColumn(name = "participating_agency_id"))
	private Set<Agency> participatingAgencies = new HashSet<>();

	public void loadFromForm(FundingOpportunity f) {
		this.setFundingType(f.getFundingType());
		this.setFrequency(f.getFrequency());
		this.setNameEn(f.getNameEn());
		this.setNameFr(f.getNameFr());
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameFr() {
		return nameFr;
	}

	public void setNameFr(String nameFr) {
		this.nameFr = nameFr;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getFundingType() {
		return fundingType;
	}

	public void setFundingType(String fundingType) {
		this.fundingType = fundingType;
	}

	public Set<Agency> getParticipatingAgencies() {
		return participatingAgencies;
	}

	public void setParticipatingAgencies(Set<Agency> participatingAgencies) {
		this.participatingAgencies = participatingAgencies;
	}

	public Boolean isJointInitiative() {
		return isJointInitiative;
	}

	public Boolean getIsJointInitiative() {
		return isJointInitiative;
	}

	public void setIsJointInitiative(Boolean isJointInitiative) {
		this.isJointInitiative = isJointInitiative;
	}

	public String getPartnerOrg() {
		return partnerOrg;
	}

	public void setPartnerOrg(String partnerOrg) {
		this.partnerOrg = partnerOrg;
	}

	public Boolean getIsEdiRequired() {
		return isEdiRequired;
	}

	public void setIsEdiRequired(Boolean isEdiRequired) {
		this.isEdiRequired = isEdiRequired;
	}

	public Boolean getIsComplex() {
		return isComplex;
	}

	public void setIsComplex(Boolean isComplex) {
		this.isComplex = isComplex;
	}

	public Boolean getIsNoi() {
		return isNoi;
	}

	public void setIsNoi(Boolean isNoi) {
		this.isNoi = isNoi;
	}

	public Boolean getIsLoi() {
		return isLoi;
	}

	public void setIsLoi(Boolean isLoi) {
		this.isLoi = isLoi;
	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	public void addAgency(Agency agency) {
		participatingAgencies.add(agency);
		agency.getFundingOpportunities().add(this);
	}
	
	public void removeAgency(Agency agency) {
		participatingAgencies.remove(agency);
		agency.getFundingOpportunities().remove(this);
	}
	
	public void removeAllAgencies() {
		Iterator<Agency> agencyIter = participatingAgencies.iterator();
		
		while (agencyIter.hasNext()) {
			Agency agency = agencyIter.next();
			agency.getFundingOpportunities().remove(this);
			agencyIter.remove();
		}
	}
	
	@Override
	public int hashCode() {
		return 2020;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) { 
			return false;
		}
		
		return id != null && id.equals(((FundingOpportunity) obj).getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FundingOpportunity [id=");
		builder.append(id);
		builder.append(", nameEn=");
		builder.append(nameEn);
		builder.append(", nameFr=");
		builder.append(nameFr);
		builder.append(", fundingType=");
		builder.append(fundingType);
		builder.append(", frequency=");
		builder.append(frequency);
		builder.append(", isJointInitiative=");
		builder.append(isJointInitiative);
		builder.append(", isNOI=");
		builder.append(isNoi);
		builder.append(", isLOI=");
		builder.append(isLoi);
		builder.append(", partnerOrg=");
		builder.append(partnerOrg);
		builder.append(", isEdiRequired=");
		builder.append(isEdiRequired);
		builder.append(", isComplex=");
		builder.append(isComplex);
		builder.append("]");
		return builder.toString();
	}

}