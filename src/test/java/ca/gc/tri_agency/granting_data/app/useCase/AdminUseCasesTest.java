package ca.gc.tri_agency.granting_data.app.useCase;

//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertEquals;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.gc.tri_agency.granting_data.app.GrantingDataApp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrantingDataApp.class)
@ActiveProfiles("test")
@PropertySource("classpath:test\\resources\\application-test.properties")
public class AdminUseCasesTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void requestSelectBackendFileForComparison_withAdminUser_shouldSucceedWith200() throws Exception {
		mvc.perform(get("/admin/selectFileForComparison").contentType(MediaType.APPLICATION_XHTML_XML))
				.andExpect(status().isOk());
	}

	@WithMockUser(username = "admin", roles = { "MDM ADMIN" })
	@Test
	public void testSelectFileForCopmarisonFilePageLinkRequests() throws Exception {
		mvc.perform(get("/admin/analyzeFoUploadData").param("filename", "NAMIS-TestFile.xlsx"))
				.andExpect(status().isOk());
	}

//	@WithMockUser(username = "admin", roles = {"MDM ADMIN"})
//	@Test
//	public void testUploadGoldenList() {
//		Path uploadFromPath = Paths.get("C:\\Temp\\grantingData");
//		assertTrue("Upload from path does not exist", Files.exists(uploadFromPath));
////		assertTrue("Upload from path does not exist", Files.exists(Paths.get("C:\\Temp\\No folder here")));
//		
//		assertTrue("Can not open the upload from directory", Files.isExecutable(uploadFromPath));
////		assertTrue("Can not open the upload from directory", Files.isExecutable(Paths.get("C:\\Temp\\Won\'t Open Dir")));
//		
//		assertTrue("Upload to path does not exist", Files.exists(Paths.get("src", "test", "resources", "datasets")));
////		assertTrue("Upload to path does not exist", Files.exists(Paths.get("src", "test", "resources", "datasets - DNE")));
//		
//		String fileNamePass = "C:\\Temp\\grantingData\\programFile.xlsm";
//		assertTrue("Upload file does not exist", Files.exists(Paths.get(fileNamePass)));
////		assertTrue("Upload file does not exist", Files.exists(Paths.get("C:\\Temp\\grantingData\\programFile DNE.xlsm")));
//		
//		assertEquals("Upload file is of an incorrect type,", "xlsm", fileNamePass.substring(fileNamePass.lastIndexOf('.') + 1));
////		String fileNameFail = "C:\\Temp\\grantingData\\programFile.txt";
////		assertEquals("Upload file is of an incorrect type,", "xlms", fileNameFail.substring(fileNameFail.lastIndexOf('.') + 1));
//		
//		assertTrue("Upload file is not readable", Files.isReadable(Paths.get(fileNamePass)));
////		assertTrue("Upload file is not readable", Files.isReadable(Paths.get("C:\\Temp\\grantingData\\programFile - Unreadable.xlsm")));
//		
//		assertTrue("Upload file can not be opened", Files.isExecutable(Paths.get(fileNamePass)));
////		assertTrue("Upload file can not be opened", Files.isExecutable(Paths.get("C:\\Temp\\grantingData\\programFile - Can't be Opened.xlsm")));
//		
//		Path outDirPath = Paths.get("src", "test", "resources", "datasets");
//		assertTrue("Can not write to the upload to directory", Files.isWritable(outDirPath));
////		assertTrue("Can not write to the upload to directory", Files.isWritable(Paths.get("C:\\Temp\\Can\'t Write to Dir")));
//		
//		assertTrue("Can not open the upload to directory", Files.isExecutable(outDirPath));
////		assertTrue("Can not open the upload to directory", Files.isExecutable(Paths.get("src", "test", "resources", "Purposely Won\'t Open")));
//		
//		Path uploadDestFilePath = Paths.get("src", "test", "resources", "datasets", "programFile.xlsm");
//		assertTrue("Upload file was not written to the correct destination", Files.exists(uploadDestFilePath));
////		assertTrue("Upload file was not written to the correct destination", 
////				Files.exists(Paths.get("src", "test", "resources", "datasets", "progs.xlsm")));
//		
//		
//		try {
//			assertArrayEquals("Upload and destination files are not the same", 
//					Files.readAllBytes(Paths.get(fileNamePass)), Files.readAllBytes(uploadDestFilePath));
////			assertArrayEquals("Upload and destination files are not the same", 
////					Files.readAllBytes(Paths.get(fileNamePass)), 
////					Files.readAllBytes(Paths.get("src", "test", "resources", "application-test.properties")));
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
//	}
//
}
