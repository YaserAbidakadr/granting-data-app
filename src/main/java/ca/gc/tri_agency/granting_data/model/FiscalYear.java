package ca.gc.tri_agency.granting_data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class FiscalYear {

	@Id
	@SequenceGenerator(name = "SEQ_FISCAL_YEAR", sequenceName = "SEQ_FISCAL_YEAR", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FISCAL_YEAR")
	private Long id;

	@NotNull(message = "{year.NotNull}")
	@Min(value = 1999, message = "{year.range}")
	@Max(value = 2050, message = "{year.range}")
	@Column(name = "year", unique = true)
	private Long year;
	
	public FiscalYear() {
	}

	public FiscalYear(Long year) {
		this.year = year;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

}
