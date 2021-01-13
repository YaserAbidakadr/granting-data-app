package ca.gc.tri_agency.granting_data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

@Entity
public class FiscalYear {

	@Id
	@SequenceGenerator(name = "SEQ_FISCAL_YEAR", sequenceName = "SEQ_FISCAL_YEAR", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FISCAL_YEAR")
	private Long id;

	@NotNull
	@Range(min = 1999, max = 2050, message = "{fy.year.range}")
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FiscalYear [id=");
		builder.append(id);
		builder.append(", year=");
		builder.append(year);
		builder.append("]");
		return builder.toString();
	}

}
