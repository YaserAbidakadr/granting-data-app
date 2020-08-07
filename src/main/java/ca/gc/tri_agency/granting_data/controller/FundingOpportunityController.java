package ca.gc.tri_agency.granting_data.controller;

import java.util.ArrayList;
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

import ca.gc.tri_agency.granting_data.form.FundingOpportunityFilterForm;
import ca.gc.tri_agency.granting_data.ldap.ADUser;
import ca.gc.tri_agency.granting_data.ldap.ADUserService;
import ca.gc.tri_agency.granting_data.model.Agency;
import ca.gc.tri_agency.granting_data.model.FundingOpportunity;
import ca.gc.tri_agency.granting_data.model.SystemFundingOpportunity;
import ca.gc.tri_agency.granting_data.security.SecurityUtils;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.AgencyService;
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

	private AgencyService agencyService;

	private SystemFundingOpportunityService sfoService;

	private BusinessUnitService buService;

	private FundingCycleService fcService;

	private ADUserService adUserService;

	private MessageSource msgSource;

	@Autowired
	public FundingOpportunityController(FundingOpportunityService foService, GrantingCapabilityService gcService,
			SystemFundingCycleService sfcService, AgencyService agencyService, SystemFundingOpportunityService sfoService,
			BusinessUnitService buService, FundingCycleService fcService, MessageSource msgSource, ADUserService adUserService) {
		this.foService = foService;
		this.gcService = gcService;
		this.sfcService = sfcService;
		this.adUserService = adUserService;
		this.agencyService = agencyService;
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

	@GetMapping("/manage/searchUser")
	public String searchUser(@RequestParam(value = "username", defaultValue = "") String username, Model model) {
		if (!username.trim().isEmpty()) {
			List<ADUser> matchingUsers = adUserService.searchADUsers(username.trim());
			model.addAttribute("matchingUsers", matchingUsers);
		}
		return "manage/searchUser";
	}

	@GetMapping("/manage/manageFo")
	public String manageFundingOpportunity(@RequestParam("id") Long id, Model model) {
		model.addAttribute("foProjections", foService.findBrowseViewFoResult(id));
		model.addAttribute("sfcProjections", sfcService.findSystemFundingCyclesByLinkedFundingOpportunity(id));
		model.addAttribute("gcProjections", gcService.findGrantingCapabilitiesForBrowseViewFO(id));
		model.addAttribute("fcProjections", fcService.findFCsForBrowseViewFO(id));
		return "manage/manageFundingOpportunity";
	}

	@AdminOnly
	@GetMapping("/manage/editFo")
	public String editFundingOpportunityGet(@RequestParam("id") Long id, Model model) {
		FundingOpportunity fo = foService.findFundingOpportunityById(id);
		model = populateAgencyOptions(model, fo);
		model.addAttribute("programForm", fo);
		return "manage/editFundingOpportunity";
	}

	@AdminOnly
	@PostMapping("/manage/editFo")
	public String editFundingOpportunityPost(@Valid @ModelAttribute("programForm") FundingOpportunity fo, BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model = populateAgencyOptions(model, fo);
			return "/manage/editFundingOpportunity";
		}
		foService.saveFundingOpportunity(fo);
		return "redirect:/browse/viewFo?id=" + fo.getId();
	}

	@GetMapping("/admin/createFo")
	public String createFundingOpportunityGet(Model model, @RequestParam("sfoId") Optional<Long> sfoId) {
		FundingOpportunity fo = new FundingOpportunity();
		if (sfoId.isPresent()) {
			SystemFundingOpportunity sfo = sfoService.findSystemFundingOpportunityById(sfoId.get());
			fo.setNameEn(sfo.getNameEn());
			fo.setNameFr(sfo.getNameFr());
		}
		model.addAttribute("fo", fo);
		model.addAttribute("allAgencies", agencyService.findAllAgencies());
		model.addAttribute("allBusinessUnits", buService.findAllBusinessUnits());
		return "admin/createFo";
	}

	@PostMapping("/admin/createFo")
	public String createFundingOpportunityPost(@Valid @ModelAttribute("fo") FundingOpportunity command, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("allAgencies", agencyService.findAllAgencies());
			return "admin/createFo";
		}
		foService.saveFundingOpportunity(command);
		String createdFo = msgSource.getMessage("h.createdFo", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMessage", createdFo + command.getLocalizedAttribute("name"));
		return "redirect:/admin/home";
	}

	private Model populateAgencyOptions(Model model, FundingOpportunity fo) {
		List<Agency> allAgencies = agencyService.findAllAgencies();
		List<Agency> otherAgencies = new ArrayList<Agency>();
		for (Agency a : allAgencies) {
			if (!fo.getParticipatingAgencies().contains(a)) {
				otherAgencies.add(a);
			}
		}
		model.addAttribute("otherAgencies", otherAgencies);
		model.addAttribute("allAgencies", allAgencies);
		return model;
	}

	@AdminOnly
	@GetMapping("/admin/auditLogFO")
	public String fundingOpportunityAuditLog(Model model) {
		model.addAttribute("revisionList", foService.findAllFundingOpportunitiesRevisions());
		return "admin/fundingOpportunityAuditLog";
	}

}
