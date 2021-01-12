package ca.gc.tri_agency.granting_data.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.form.FormErrorCountIterator;
import ca.gc.tri_agency.granting_data.model.GrantingCapability;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;
import ca.gc.tri_agency.granting_data.service.GrantingStageService;
import ca.gc.tri_agency.granting_data.service.GrantingSystemService;

@Controller
public class GrantingCapabilityController {

	private GrantingCapabilityService gcService;

	private GrantingStageService gStageService;

	private GrantingSystemService gSystemService;

	private MessageSource msgSource;

	@Autowired
	public GrantingCapabilityController(GrantingCapabilityService gcService, GrantingStageService gStageService,
			GrantingSystemService gSystemService, MessageSource msgSource) {
		this.gcService = gcService;
		this.gStageService = gStageService;
		this.gSystemService = gSystemService;
		this.msgSource = msgSource;
	}

	@AdminOnly
	@GetMapping("/manage/editGC")
	public String editGrantingCapabilityGet(@RequestParam("id") Long id, Model model) {
		model.addAttribute("gc", gcService.findGrantingCapabilityAndFO(id));
		model.addAttribute("grantingStages", gStageService.findAllGrantingStages());
		model.addAttribute("grantingSystems", gSystemService.findAllGrantingSystems());

		return "manage/editGrantingCapability";
	}

	@AdminOnly
	@PostMapping("/manage/editGC")
	public String editGrantingCapabilityPost(@Valid @ModelAttribute("gc") GrantingCapability gc, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("grantingStages", gStageService.findAllGrantingStages());
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
	public String deleteGrantingCapabilityGet(@RequestParam("id") Long id, Model model) {
		model.addAttribute("gc", gcService.findGrantingCapabilityById(id));

		return "manage/deleteGrantingCapability";
	}

	@AdminOnly
	@PostMapping("/manage/deleteGC")
	public String deleteGrantingCapabilityPost(@RequestParam("id") Long id, @RequestParam("foId") Long foId,
			RedirectAttributes redirectAttributes) {
		gcService.deleteGrantingCapabilityById(id);

		String actionMsg = msgSource.getMessage("h.deletedGC", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/manage/manageFo?id=" + foId;
	}

	@AdminOnly
	@GetMapping("/manage/addGrantingCapabilities")
	public String createGrantingCapabilityGet(@RequestParam("foId") Long foId, Model model) {
		model.addAttribute("gc", new GrantingCapability());
		model.addAttribute("foId", foId);
		model.addAttribute("grantingSystems", gSystemService.findAllGrantingSystems());
		model.addAttribute("grantingStages", gStageService.findAllGrantingStages());

		return "manage/addGrantingCapabilities";
	}

	@AdminOnly
	@PostMapping("/manage/addGrantingCapabilities")
	public String createGrantingCapabilityPost(@RequestParam("fundingOpportunity") Long foId, @Valid @ModelAttribute("gc") GrantingCapability gc,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("foId", foId);
			model.addAttribute("grantingStages", gStageService.findAllGrantingStages());
			model.addAttribute("grantingSystems", gSystemService.findAllGrantingSystems());

			return "manage/addGrantingCapabilities";
		}

		gcService.saveGrantingCapability(gc);

		String actionMsg = msgSource.getMessage("h.createdGc", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/manage/manageFo?id=" + gc.getFundingOpportunity().getId();
	}

}
