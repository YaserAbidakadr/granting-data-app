package ca.gc.tri_agency.granting_data.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.model.file.FundingCycleDatasetRow;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.AdminService;

@Controller
@RequestMapping("/admin")
@AdminOnly
public class AdminController {

	private AdminService adminService;
	
	@Autowired
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/selectFileForComparison")
	public String compareData_selectDatasetUploadFile(Model model) {
		model.addAttribute("datasetFiles", adminService.getDatasetFiles());
		return "admin/selectFileForComparison";
	}

	@GetMapping("/analyzeFoUploadData")
	public String compareData_showDatasetUploadAdditions(@RequestParam("filename") String filename, Model model) {
		model.addAttribute("filename", filename);
		List<FundingCycleDatasetRow> fileRows = adminService.getFundingCyclesFromFile(filename);
		model.addAttribute("fileRows", fileRows);
		model.addAttribute("actionRowIds", adminService.generateActionableFoCycleIds(fileRows));
		return "admin/analyzeFoUploadData";
	}

	@PostMapping("/analyzeFoUploadData")
	public String compareData_uploadSelectedNames_post(@RequestParam String filename,
			@RequestParam("idToAction") String[] idsToAction, final RedirectAttributes redirectAttrs) {
		long numChances = adminService.applyChangesFromFileByIds(filename, idsToAction);
		redirectAttrs.addFlashAttribute("actionMessage", "Successfully applied " + numChances + " Funcing Cycles");
		return "redirect:/admin/home";
	}

	@GetMapping("/home")
	public String home() {
		return "admin/home";
	}

	@GetMapping("/auditLogs")
	public String auditLogs() {
		return "admin/auditLogs";
	}
	
}
