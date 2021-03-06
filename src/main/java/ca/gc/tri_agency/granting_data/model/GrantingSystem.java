package ca.gc.tri_agency.granting_data.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.envers.Audited;

import ca.gc.tri_agency.granting_data.model.util.LocalizedParametersModel;

@Entity
@Audited
public class GrantingSystem implements LocalizedParametersModel {
	
	@Id
	@SequenceGenerator(name = "SEQ_GRANTING_SYSTEM", sequenceName = "SEQ_GRANTING_SYSTEM", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GRANTING_SYSTEM")
	private Long id;

	private String nameEn;

	private String nameFr;

	private String acronym;

	public GrantingSystem() {
	}

	public GrantingSystem(String nameEn, String nameFr, String acronymEn, String acronymnFr) {
		this.setNameEn(nameEn);
		this.setNameFr(nameFr);
		this.setAcronym(acronym);
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

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

}
