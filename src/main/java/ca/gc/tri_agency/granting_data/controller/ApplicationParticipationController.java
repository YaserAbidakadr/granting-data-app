package ca.gc.tri_agency.granting_data.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.model.projection.ApplicationParticipationProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.service.ApplicationParticipationService;

@Controller
public class ApplicationParticipationController {

	private ApplicationParticipationService apService;

	@Autowired
	public ApplicationParticipationController(ApplicationParticipationService apService) {
		this.apService = apService;
	}

	@GetMapping("/browse/appParticipations")
	public String appParticipations(Model model) {
		model.addAttribute("appParticipations", apService.findAppPartsForCurrentUserWithEdiAuth());
		return "browse/appParticipations";
	}

	@GetMapping("/browse/viewAppParticipationEdi")
	public String appParticipationEdi(@RequestParam("id") Long id, Model model) throws AccessDeniedException {
		List<ApplicationParticipationProjection> apProjections = apService.findAppPartWithEdiData(id);

		model.addAttribute("appParticipation", apProjections.get(0));
		model.addAttribute("indIdentities", apProjections.stream().filter(ap -> ap.getIndIdentityId() != null)
				.filter(Utility.isSubProjectionIdUnique(ApplicationParticipationProjection::getIndIdentityId)).collect(Collectors.toList()));
		model.addAttribute("vMinorities", apProjections.stream().filter(ap -> ap.getVisMinorityId() != null)
				.filter(Utility.isSubProjectionIdUnique(ApplicationParticipationProjection::getVisMinorityId)).collect(Collectors.toList()));
		
		return "browse/viewAppParticipationEdi";
	}

	@GetMapping("/admin/generateTestParticipations")
	public String generateTestParticipations() {
		return "admin/generateTestParticipations";
	}

	@PostMapping("/admin/generateTestParticipations")
	public String post_generateTestParticipations(RedirectAttributes redirectAttrs) {
		long numCreated = apService.generateTestAppParicipationsForAllSystemFundingOpportunities();
		redirectAttrs.addFlashAttribute("actionMsg", "Successfully created " + numCreated + " Test App Participations");
		return "redirect:/home";
	}

	@GetMapping("/browse/viewAP")
	public String viewOneApplicationParticipation(@RequestParam("id") Long apId, Model model) throws AccessDeniedException {
		model.addAttribute("ap", apService.findAppPartById(apId));
		return "/browse/viewAppParticipation";
	}

}
