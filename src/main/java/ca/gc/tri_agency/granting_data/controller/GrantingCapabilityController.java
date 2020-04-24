package ca.gc.tri_agency.granting_data.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.repo.GrantingStageRepository;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;

@Controller
public class GrantingCapabilityController {

	private GrantingCapabilityService gcService;
	private GrantingStageRepository gStageRepo; // TODO: REFACTOR
	private GrantingSystemService gSystemService;

	@Autowired
	private MessageSource msgSource;

	@Autowired
	public GrantingCapabilityController(GrantingCapabilityService gcService, GrantingStageRepository gStageRepo,
			GrantingSystemService gSystemService) {
		this.gcService = gcService;
		this.gStageRepo = gStageRepo;
		this.gSystemService = gSystemService;
	}

	@AdminOnly
	@GetMapping("/manage/editGC")
	public String accessEditGC(@RequestParam("id") Long id, Model model) {
		model.addAttribute("gc", gcService.findGrantingCapabilityById(id));
		model.addAttribute("grantingStages", gStageRepo.findAll());
		model.addAttribute("grantingSystems", gSystemService.findAllGrantingSystems());
		return "manage/editGrantingCapability";
	}

	@AdminOnly
	@PostMapping("/manage/editGC")
	public String processEditGC(@Valid @ModelAttribute("gc") GrantingCapability gc, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("grantingStages", gStageRepo.findAll());
			model.addAttribute("grantingSystems", gSystemService.findAllGrantingSystems());
			return "manage/editGrantingCapability";
		}
		gcService.saveGrantingCapability(gc);
		String actionMsg = msgSource.getMessage("h.editedGc", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);
		return "redirect:/manage/manageFo?id=" + gc.getFundingOpportunity().getId();
	}

	@AdminOnly
	@GetMapping("/manage/deleteGC")
	public String accessDeleteGC(@RequestParam("id") Long id, Model model) {
		model.addAttribute("gc", gcService.findGrantingCapabilityById(id));
		return "manage/deleteGrantingCapability";
	}

	@AdminOnly
	@PostMapping("/manage/deleteGC")
	public String processDeleteGC(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
		String foId = gcService.findGrantingCapabilityById(id).getFundingOpportunity().getId().toString();
		gcService.deleteGrantingCapabilityById(id);
		String actionMsg = msgSource.getMessage("h.deletedGC", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);
		return "redirect:/manage/manageFo?id=" + foId;
	}

	@AdminOnly
	@GetMapping("/manage/addGrantingCapabilities")
	public String accessCreateGC(@RequestParam("foId") long foId, Model model) {
		model.addAttribute("foId", foId);
		model.addAttribute("gc", new GrantingCapability());
		model.addAttribute("grantingSystems", gSystemService.findAllGrantingSystems());
		model.addAttribute("grantingStages", gStageRepo.findAll());
		return "manage/addGrantingCapabilities";
	}

	@AdminOnly
	@PostMapping("/manage/addGrantingCapabilities")
	public String processCreateGC(@Valid @ModelAttribute("gc") GrantingCapability command,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			for (ObjectError br : bindingResult.getAllErrors()) {
				System.out.println(br.toString());
			}
			return "manage/addGrantingCapabilities";
		}
		gcService.saveGrantingCapability(command);
		String actionMsg = msgSource.getMessage("h.createdGc", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);
		return "redirect:/browse/viewFo?id=" + command.getFundingOpportunity().getId();
	}
	
}
