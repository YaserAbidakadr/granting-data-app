package ca.gc.tri_agency.granting_data.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

@Entity
public class GrantingCapability {

	@Id
	@SequenceGenerator(name = "SEQ_GRANTING_CAPABILITY", sequenceName = "SEQ_GRANTING_CAPABILITY", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GRANTING_CAPABILITY")
	private Long id;

	@NotBlank
	private String description;

	@URL
	@NotBlank
	private String url;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funding_opportunity_id")
	private FundingOpportunity fundingOpportunity;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "granting_stage_id")
	private GrantingStage grantingStage;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "granting_system_id")
	private GrantingSystem grantingSystem;

	public GrantingCapability() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FundingOpportunity getFundingOpportunity() {
		return fundingOpportunity;
	}

	public void setFundingOpportunity(FundingOpportunity program) {
		this.fundingOpportunity = program;
	}

	public GrantingStage getGrantingStage() {
		return grantingStage;
	}

	public void setGrantingStage(GrantingStage grantingStage) {
		this.grantingStage = grantingStage;
	}

	public GrantingSystem getGrantingSystem() {
		return grantingSystem;
	}

	public void setGrantingSystem(GrantingSystem grantingSystem) {
		this.grantingSystem = grantingSystem;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

		return id != null && id.equals(((GrantingCapability) obj).getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GrantingCapability [id=");
		builder.append(id);
		builder.append(", description=");
		builder.append(description);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}

}
