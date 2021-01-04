package ca.gc.tri_agency.granting_data.ldap;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Entry(objectClasses = "person")
public final class ADUser {

	@Id
	private Name dn;

	@Attribute(name = "sAMAccountName", readonly = true)
	private String userLogin;

	@Attribute(name = "cn", readonly = true)
	private String fullName;

	@Attribute(name = "sn", readonly = true)
	private String lastName;

	@Attribute(name = "mail", readonly = true)
	private String email;

	@Attribute(name = "uid", readonly = true)
	private String ldapUserLogin;

	public ADUser() {
	}

	public Name getDn() {
		return dn;
	}

	public String getUserLogin() {
		if (ldapUserLogin != null) {
			userLogin = ldapUserLogin;
		}
		
		return userLogin;
	}

	public String getFullName() {
		return fullName;
	}

	public String getLastName() {
		return null;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ADUser [dn=");
		builder.append(dn);
		builder.append(", userLogin=");
		builder.append(userLogin);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(email);
		builder.append("]");
		return builder.toString();
	}

}
