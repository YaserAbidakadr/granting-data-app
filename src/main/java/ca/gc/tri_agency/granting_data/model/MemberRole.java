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

@Entity
@Audited
public class MemberRole {

	@Id
	@SequenceGenerator(name = "SEQ_MEMBER_ROLE", sequenceName = "SEQ_MEMBER_ROLE", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_ROLE")
	private Long id;

	@NotBlank
	private String userLogin;

	@NotNull
	private Boolean ediAuthorized;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	@NotNull
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_unit_id")
	private BusinessUnit businessUnit;

	public MemberRole() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	public Boolean getEdiAuthorized() {
		return ediAuthorized;
	}

	public void setEdiAuthorized(Boolean ediAuthorized) {
		this.ediAuthorized = ediAuthorized;
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
		
		return id != null && id.equals(((MemberRole) obj).getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberRole [id=");
		builder.append(id);
		builder.append(", userLogin=");
		builder.append(userLogin);
		builder.append(", ediAuthorized=");
		builder.append(ediAuthorized);
		builder.append("]");
		return builder.toString();
	}
	
}
