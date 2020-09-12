package ca.gc.tri_agency.granting_data.controller;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

	private MessageSource msgSource;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job excelFundingCycleUploadJob;

	@Value("${dataset.analysis.folder}")
	private String datasetAnalysisFolder;

	@Autowired
	public AdminController(AdminService adminService, MessageSource msgSource) {
		this.adminService = adminService;
		this.msgSource = msgSource;
	}

	@GetMapping("/selectFileForComparison")
	public String compareData_selectDatasetUploadFile(Model model) {
		model.addAttribute("datasetFiles", adminService.getDatasetFiles());
		return "admin/selectFileForComparison";
	}

	@GetMapping("/analyzeFoUploadData")
	public String compareData_showDatasetUploadAdditions(@RequestParam("filename") String fileName, Model model)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
			JobParametersInvalidException, Exception {
		model.addAttribute("filename", fileName);
		List<FundingCycleDatasetRow> fileRows = adminService.getFundingCyclesFromFile(fileName);
		model.addAttribute("fileRows", fileRows);
		model.addAttribute("actionRowIds", adminService.generateActionableFoCycleIds(fileRows));

		// BATCH JOB
		JobParameters jobParams = new JobParametersBuilder().addDate("run.date", Date.from(Instant.now()))
				.addString("file.name", datasetAnalysisFolder + fileName).toJobParameters();

		jobLauncher.run(excelFundingCycleUploadJob, jobParams);

		return "admin/analyzeFoUploadData";
	}

	@PostMapping("/analyzeFoUploadData")
	public String compareData_uploadSelectedNames_post(@RequestParam String filename, @RequestParam("idToAction") String[] idsToAction,
			final RedirectAttributes redirectAttrs) {
		long numChances = adminService.applyChangesFromFileByIds(filename, idsToAction);
		String actionMsg = msgSource.getMessage("msg.successfullyRegisteredFCs", new Object[] { new Long(numChances) },
				LocaleContextHolder.getLocale());
		redirectAttrs.addFlashAttribute("actionMsg", actionMsg);
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
