package ca.gc.tri_agency.granting_data.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.envers.Audited;

import ca.gc.tri_agency.granting_data.model.util.LocalizedParametersModel;

@Entity
@Audited
public class SystemFundingOpportunity implements LocalizedParametersModel {

	@Id
	@SequenceGenerator(name = "SEQ_SYSTEM_FUNDING_OPPORTUNITY", sequenceName = "SEQ_SYSTEM_FUNDING_OPPORTUNITY", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SYSTEM_FUNDING_OPPORTUNITY")
	private Long id;

	private String extId;

	private String nameEn;

	private String nameFr;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "linked_funding_opportunity_id")
	private FundingOpportunity linkedFundingOpportunity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "granting_system_id")
	private GrantingSystem grantingSystem;

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

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public GrantingSystem getGrantingSystem() {
		return grantingSystem;
	}

	public void setGrantingSystem(GrantingSystem grantingSystem) {
		this.grantingSystem = grantingSystem;
	}

	public FundingOpportunity getLinkedFundingOpportunity() {
		return linkedFundingOpportunity;
	}

	public void setLinkedFundingOpportunity(FundingOpportunity linkedFundingOpportunity) {
		this.linkedFundingOpportunity = linkedFundingOpportunity;
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

		return id != null && id.equals(((SystemFundingOpportunity) obj).getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SystemFundingOpportunity [id=");
		builder.append(id);
		builder.append(", extId=");
		builder.append(extId);
		builder.append(", nameEn=");
		builder.append(nameEn);
		builder.append(", nameFr=");
		builder.append(nameFr);
		builder.append("]");
		return builder.toString();
	}

}
