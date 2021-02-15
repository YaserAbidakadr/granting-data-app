package ca.gc.tri_agency.granting_data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.ApplicationParticipationService;
import ca.gc.tri_agency.granting_data.service.AwardService;

@Controller
public class AwardController {

	private AwardService awardService;

	private ApplicationParticipationService appPartService;

	@Autowired
	public AwardController(AwardService awardService, ApplicationParticipationService appPartService) {
		this.awardService = awardService;
		this.appPartService = appPartService;
	}

	@AdminOnly
	@GetMapping("/admin/generateTestAwards")
	public String generateTestAwardsGet() {
		return "admin/generateTestAwards";
	}

	@AdminOnly
	@PostMapping("/admin/generateTestAwards")
	public String generateTestAwardsPost(RedirectAttributes redirectAttributes) {
		int numAwards = awardService.generateTestAwards(appPartService.getAllowedRecords(), 10).size();
		redirectAttributes.addFlashAttribute("actionMsg",
				"Successfully created Awards for " + numAwards + " of the Test App Participations");
		
		return "redirect:/home";
	}

	@GetMapping("/browse/awards")
	public String browseAwardsGet(Model model) {
		model.addAttribute("awardList", awardService.findAwardsForCurrentUser());
		return "browse/awards";
	}

}
