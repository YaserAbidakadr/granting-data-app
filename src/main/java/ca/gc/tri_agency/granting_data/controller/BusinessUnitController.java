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
import ca.gc.tri_agency.granting_data.model.BusinessUnit;
import ca.gc.tri_agency.granting_data.security.SecurityUtils;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.AgencyService;
import ca.gc.tri_agency.granting_data.service.BusinessUnitService;

@Controller
public class BusinessUnitController {

	private BusinessUnitService buService;

	private AgencyService agencyService;

	private MessageSource msgSource;

	@Autowired
	public BusinessUnitController(BusinessUnitService buService, AgencyService agencyService, MessageSource msgSource) {
		this.buService = buService;
		this.agencyService = agencyService;
		this.msgSource = msgSource;

	}

	@GetMapping("/browse/viewBU")
	public String viewBusinessUnit(@RequestParam("id") Long id, Model model) {
		model.addAttribute("buProjections", buService.findResultsForBrowseViewBU(id));

		if (SecurityUtils.isCurrentUserAdmin()) {
			model.addAttribute("revisionList", buService.findBusinessUnitRevisionsById(id));
		}

		return "browse/viewBU";
	}

	@AdminOnly
	@GetMapping("/admin/createBU")
	public String createBusinessUnitGet(@RequestParam("agencyId") Long agencyId, Model model) {
		model.addAttribute("bu", new BusinessUnit());
		model.addAttribute("agency", agencyService.findAgencyName(agencyId));
		
		return "admin/createBU";
	}

	@AdminOnly
	@PostMapping("/admin/createBU")
	public String createBusinessUnitPost(@RequestParam("agency") Long agencyId, @Valid @ModelAttribute("bu") BusinessUnit bu, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("agency", agencyService.findAgencyName(agencyId));
			
			return "admin/createBU";
		}
		
		buService.saveBusinessUnit(bu);
		
		String actionMsg = msgSource.getMessage("h.createdBu", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg + bu.getLocalizedAttribute("name"));
		
		return "redirect:/browse/viewAgency?id=" + bu.getAgency().getId();
	}

	@AdminOnly
	@GetMapping("/admin/editBU")
	public String editBusinessUnitGet(@RequestParam("id") Long id, Model model) {
		model.addAttribute("bu", buService.findBusinessUnitWithAgency(id));
		
		return "admin/editBU";
	}

	@AdminOnly
	@PostMapping("/admin/editBU")
	public String editBusinessUnitPost(@Valid @ModelAttribute("bu") BusinessUnit bu, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/editBU";
		}
		
		buService.saveBusinessUnit(bu);
		
		String actionMsg = msgSource.getMessage("h.editedBu", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/browse/viewBU?id=" + bu.getId();
	}

}
