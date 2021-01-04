package ca.gc.tri_agency.granting_data.ldap;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

@Service
public class ADUserService {

	private LdapTemplate ldapTemplateNSERC;

	private LdapTemplate ldapTemplateSSHRC;

	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	public ADUserService(@Qualifier("ldapTemplateNSERC") LdapTemplate ldapTemplateNSERC,
			@Qualifier("ldapTemplateSSHRC") LdapTemplate ldapTemplateSSHRC) {
		this.ldapTemplateNSERC = ldapTemplateNSERC;
		this.ldapTemplateSSHRC = ldapTemplateSSHRC;
	}

	public List<ADUser> findAllADUsers() {
		List<ADUser> retval = ldapTemplateNSERC.findAll(ADUser.class);
		retval.addAll(ldapTemplateSSHRC.findAll(ADUser.class));
		return retval;
	}

	public List<String> findAllADUserFullNames() {
		LdapQuery findAllPeronsQuery = LdapQueryBuilder.query().where("objectclass").is("person");
		AttributesMapper<String> fullNameMapper = new AttributesMapper<String>() {
			@Override
			public String mapFromAttributes(Attributes attributes) throws NamingException {
				return (String) attributes.get("cn").get();
			}
		};

		List<String> names = ldapTemplateNSERC.search(findAllPeronsQuery, fullNameMapper);
		names.addAll(ldapTemplateSSHRC.search(findAllPeronsQuery, fullNameMapper));

		return names;
	}

	public ADUser findADUserByDn(String dn) {
		ADUser adUser = null;

		try {
			adUser = ldapTemplateNSERC.findByDn(LdapUtils.newLdapName(dn), ADUser.class);
		} catch (NameNotFoundException nnfe) {
			LOG.info("No NSERC user has dn=" + dn);
		}

		if (null == adUser) {
			try {
				adUser = ldapTemplateSSHRC.findByDn(LdapUtils.newLdapName(dn), ADUser.class);
			} catch (NameNotFoundException nnfe) {
				LOG.info("No SSHRC user has dn=" + dn);
			}
		}

		return adUser;
	}

	public String findDnByADUserLogin(String userLogin) {
		String dn = null;
		LdapQuery findByUidQuery = LdapQueryBuilder.query().countLimit(1).where("objectclass").is("person").and("sAMAccountName")
				.is(userLogin);

		try {
			dn = ldapTemplateNSERC.findOne(findByUidQuery, ADUser.class).getDn().toString();
		} catch (EmptyResultDataAccessException erdae) {
			LOG.info("No NSERC user has sAMAccountName=" + userLogin);

		}

		if (null == dn) {
			try {
				dn = ldapTemplateSSHRC.findOne(findByUidQuery, ADUser.class).getDn().toString();
			} catch (EmptyResultDataAccessException erdae) {
				LOG.info("No SSHRC user has sAMAccountName=" + userLogin);
			}
		}

		return dn;
	}

	public List<ADUser> searchADUsers(String searchStr) {
		searchStr = "*" + searchStr + "*";
		LdapQuery searchQuery = LdapQueryBuilder.query().countLimit(50).where("sAMAccountName").like(searchStr).or("cn").like(searchStr)
				.or("sn").like(searchStr);

		List<ADUser> adUsers = ldapTemplateNSERC.find(searchQuery, ADUser.class);
		adUsers.addAll(ldapTemplateSSHRC.find(searchQuery, ADUser.class));

		return adUsers;

	}

}
