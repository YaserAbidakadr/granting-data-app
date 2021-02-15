package ca.gc.tri_agency.granting_data.controller;

import java.util.List;
import java.util.Optional;

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
import ca.gc.tri_agency.granting_data.form.FundingOpportunityFilterForm;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.security.SecurityUtils;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.BusinessUnitService;
import ca.gc.tri_agency.granting_data.service.FundingCycleService;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;
import ca.gc.tri_agency.granting_data.service.GrantingCapabilityService;
import ca.gc.tri_agency.granting_data.service.SystemFundingCycleService;
import ca.gc.tri_agency.granting_data.service.SystemFundingOpportunityService;

@Controller
public class FundingOpportunityController {

	private FundingOpportunityService foService;

	private GrantingCapabilityService gcService;

	private SystemFundingCycleService sfcService;

	private SystemFundingOpportunityService sfoService;

	private BusinessUnitService buService;

	private FundingCycleService fcService;

	private MessageSource msgSource;

	@Autowired
	public FundingOpportunityController(FundingOpportunityService foService, GrantingCapabilityService gcService,
			SystemFundingCycleService sfcService, SystemFundingOpportunityService sfoService,
			BusinessUnitService buService, FundingCycleService fcService, MessageSource msgSource) {
		this.foService = foService;
		this.gcService = gcService;
		this.sfcService = sfcService;
		this.sfoService = sfoService;
		this.fcService = fcService;
		this.buService = buService;
		this.msgSource = msgSource;
	}

	@GetMapping("/browse/fundingOpportunities")
	public String viewGoldenList(@ModelAttribute("filter") FundingOpportunityFilterForm filter, Model model) {
		List<String[]> fos = foService.findGoldenListTableResults();
		model.addAttribute("fundingOpportunities", fos);

		// filtering options
		model.addAttribute("distinctBUsEn",
				fos.stream().map(fo -> fo[3]).distinct().filter(bu -> bu != null && !bu.trim().isEmpty()).sorted().iterator());
		model.addAttribute("distinctBUsFr",
				fos.stream().map(fo -> fo[4]).distinct().filter(bu -> bu != null && !bu.trim().isEmpty()).sorted().iterator());
		model.addAttribute("distinctApplySystems", fos.stream().map(fo -> fo[5]).distinct()
				.filter(appSys -> appSys != null && !appSys.trim().isEmpty()).sorted().iterator());
		model.addAttribute("distinctAwardSystems", fos.stream().map(fo -> fo[6]).distinct()
				.filter(awdSys -> awdSys != null && !awdSys.trim().isEmpty()).sorted().iterator());

		return "browse/fundingOpportunities";
	}

	@GetMapping("/browse/viewFo")
	public String viewFundingOpportunity(@RequestParam("id") Long id, Model model) {
		model.addAttribute("foProjections", foService.findBrowseViewFoResult(id)); // returns List b/c one FO can have multiple Agencies
		model.addAttribute("gcProjections", gcService.findGrantingCapabilitiesForBrowseViewFO(id));
		model.addAttribute("sfcProjections", sfcService.findSystemFundingCyclesByLinkedFundingOpportunity(id));
		model.addAttribute("fcProjections", fcService.findFCsForBrowseViewFO(id));

		if (SecurityUtils.isCurrentUserAdmin()) {
			model.addAttribute("revisionList", foService.findFundingOpportunityRevisionsById(id));
		}

		return "browse/viewFundingOpportunity";
	}

	@GetMapping("/manage/manageFo")
	public String manageFundingOpportunity(@RequestParam("id") Long id, Model model) {
		model.addAttribute("foProjections", foService.findBrowseViewFoResult(id));
		model.addAttribute("gcProjections", gcService.findGrantingCapabilitiesForBrowseViewFO(id));
		model.addAttribute("sfcProjections", sfcService.findSystemFundingCyclesByLinkedFundingOpportunity(id));
		model.addAttribute("fcProjections", fcService.findFCsForBrowseViewFO(id));

		return "manage/manageFundingOpportunity";
	}

	@AdminOnly
	@GetMapping("/manage/editFo")
	public String editFundingOpportunityGet(@RequestParam("id") Long id, Model model) {
		model.addAttribute("fo", foService.findFundingOpportunityEager(id).get(0));

		return "manage/editFundingOpportunity";
	}

	@AdminOnly
	@PostMapping("/manage/editFo")
	public String editFundingOpportunityPost(@Valid @ModelAttribute("fo") FundingOpportunity fo, BindingResult bindingResult,
			RedirectAttributes redirectAttributes,  Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			
			return "/manage/editFundingOpportunity";
		}
		
		foService.saveFundingOpportunity(fo);
		
		String actionMsg = msgSource.getMessage("h.editedFO", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);
		
		return "redirect:/browse/viewFo?id=" + fo.getId();
	}

	@AdminOnly
	@GetMapping("/admin/createFO")
	public String createFundingOpportunityGet(Model model, @RequestParam("sfoId") Optional<Long> sfoId) {
		FundingOpportunity fo = new FundingOpportunity();
		if (sfoId.isPresent()) {
			SystemFundingOpportunity sfo = sfoService.findSystemFundingOpportunityById(sfoId.get());
			fo.setNameEn(sfo.getNameEn());
			fo.setNameFr(sfo.getNameFr());
		}

		model.addAttribute("fo", fo);
		model.addAttribute("allBusinessUnits", buService.findAllBusinessUnits());
		
		return "admin/createFundingOpportunity";
	}

	@AdminOnly
	@PostMapping("/admin/createFO")
	public String createFundingOpportunityPost(@Valid @ModelAttribute("fo") FundingOpportunity fo, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("allBusinessUnits", buService.findAllBusinessUnits());
			
			return "admin/createFundingOpportunity";
		}
		
		foService.saveFundingOpportunity(fo);
		
		String actionMsg = msgSource.getMessage("h.createdFo", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg + fo.getLocalizedAttribute("name"));
		
		return "redirect:/browse/fundingOpportunities";
	}

//	private Model populateAgencyOptions(Model model, FundingOpportunity fo) {
//		List<Agency> allAgencies = agencyService.findAllAgencies();
//		List<Agency> otherAgencies = new ArrayList<Agency>();
//		for (Agency a : allAgencies) {
//			if (!fo.getParticipatingAgencies().contains(a)) {
//				otherAgencies.add(a);
//			}
//		}
//		model.addAttribute("otherAgencies", otherAgencies);
//		model.addAttribute("allAgencies", allAgencies);
//		return model;
//	}

	@AdminOnly
	@GetMapping("/admin/auditLogFO")
	public String fundingOpportunityAuditLog(Model model) {
		model.addAttribute("revisionList", foService.findAllFundingOpportunitiesRevisions());
		return "admin/fundingOpportunityAuditLog";
	}

}
