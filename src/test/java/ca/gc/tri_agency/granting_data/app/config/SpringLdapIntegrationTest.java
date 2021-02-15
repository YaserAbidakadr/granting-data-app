package ca.gc.tri_agency.granting_data.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;
import ca.gc.tri_agency.granting_data.ldap.ADUser;
import ca.gc.tri_agency.granting_data.ldap.ADUserService;

@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
public class SpringLdapIntegrationTest {

	@Autowired
	private ADUserService adUserService;

	@Tag("active_directory_configuration_test")
	@Test
	public void testFindAllADUsers() {
		List<ADUser> users = adUserService.findAllADUsers();
		assertNotNull(users);
		assertEquals(9, users.size());
	}

	@Tag("active_directory_configuration_test")
	@Test
	public void testFindAllADUserFullNames() {
		List<String> persons = adUserService.findAllADUserFullNames();
		assertNotNull(persons);
		assertEquals(9, persons.size());
	}

	@Tag("active_directory_configuration_test")
	@Test
	public void testFindNsercADUser() {
		ADUser user = adUserService.findADUserByDn("uid=admin,ou=NSERC_Users");
		assertNotNull(user);
		assertEquals("Admin User", user.getFullName());
		
		// verify NameNotFoundException handling
		assertNull(adUserService.findADUserByDn("uid=ZZZZZ,ou=ZZZZZ"));
	}

	@Tag("active_directory_configuration_test")
	@Test
	public void testFindSshrcADUser() {
		ADUser user = adUserService.findADUserByDn("uid=sshrc-admin,ou=SSHRC1_Users");
		assertNotNull(user);
		assertEquals("SSHRC Admin", user.getFullName());
	}
	
	@Tag("user_story_19187")
	@Test
	public void testSearchADUsersForMemberRoleCreation() {
		assertEquals(6, adUserService.searchADUsers("user").size());
		assertEquals(0, adUserService.searchADUsers("ZZZZZ").size());
	}

}
