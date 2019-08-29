package ca.gc.tri_agency.granting_data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GrantingStage {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "granting_function")
	@Enumerated(EnumType.STRING)
	private GrantingFunction grantingFunction;

	public enum GrantingFunction {
		/** Some other format. */
		ADMIN("admin"), APPLY("apply"), ASSESS("assess"), AWARD("award"), AQUIT("aquit");

		private final String displayValue;

		private GrantingFunction(String displayValue) {
			this.displayValue = displayValue;
		}

		public String getDisplayValue() {
			return displayValue;
		}
	}

	public GrantingStage() {

	}

	public Long getId() {
		return id;
	}

	public GrantingFunction getGrantingFunction() {
		return grantingFunction;
	}

	public void setGrantingFunction(GrantingFunction grantingFunction) {
		this.grantingFunction = grantingFunction;
	}

}
