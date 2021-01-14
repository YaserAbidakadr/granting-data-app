package ca.gc.tri_agency.granting_data.model;

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

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import ca.gc.tri_agency.granting_data.model.util.LocalizedParametersModel;

@Entity
@Audited
public class BusinessUnit implements LocalizedParametersModel {

	@Id
	@SequenceGenerator(name = "SEQ_BUSINESS_UNIT", sequenceName = "SEQ_BUSINESS_UNIT", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BUSINESS_UNIT")
	private Long id;

	@NotBlank
	private String nameEn;

	@NotBlank
	private String nameFr;

	@NotBlank
	private String acronymEn;

	@NotBlank
	private String acronymFr;

	// email address
	private String distribution;

	@NotNull
	@NotAudited
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "agency_id")
	private Agency agency;

	public BusinessUnit() {
	}

	public BusinessUnit(String nameEn, String nameFr, String acronymEn, String acronymFr, String distribution, Agency agency) {
		this.nameEn = nameEn;
		this.nameFr = nameFr;
		this.acronymEn = acronymEn;
		this.acronymFr = acronymFr;
		this.agency = agency;
		this.distribution = distribution;
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

	public String getAcronymEn() {
		return acronymEn;
	}

	public void setAcronymEn(String acronymEn) {
		this.acronymEn = acronymEn;
	}

	public String getAcronymFr() {
		return acronymFr;
	}

	public void setAcronymFr(String acronymFr) {
		this.acronymFr = acronymFr;
	}

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BusinessUnit [id=");
		builder.append(id);
		builder.append(", nameEn=");
		builder.append(nameEn);
		builder.append(", nameFr=");
		builder.append(nameFr);
		builder.append(", acronymEn=");
		builder.append(acronymEn);
		builder.append(", acronymFr=");
		builder.append(acronymFr);
		builder.append(", distribution=");
		builder.append(distribution);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		BusinessUnit other = (BusinessUnit) obj;
		return id != null && id.equals(other.getId());
	}

	@Override
	public int hashCode() {
		return 2020;
	}
}
