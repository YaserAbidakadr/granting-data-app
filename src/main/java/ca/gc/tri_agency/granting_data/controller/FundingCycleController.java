package ca.gc.tri_agency.granting_data.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.form.FormErrorCountIterator;
import ca.gc.tri_agency.granting_data.model.FundingCycle;
import ca.gc.tri_agency.granting_data.model.projection.FundingCycleProjection;
import ca.gc.tri_agency.granting_data.model.util.CalendarGrid;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.security.SecurityUtils;
import ca.gc.tri_agency.granting_data.service.FiscalYearService;
import ca.gc.tri_agency.granting_data.service.FundingCycleService;
import ca.gc.tri_agency.granting_data.service.FundingOpportunityService;
import ca.gc.tri_agency.granting_data.service.MemberRoleService;

@Controller
public class FundingCycleController {

	private FundingCycleService fcService;

	private FiscalYearService fyService;

	private MemberRoleService mrService;

	private FundingOpportunityService foService;

	private MessageSource msgSource;

	@Autowired
	public FundingCycleController(FundingCycleService fcService, FiscalYearService fyService, MemberRoleService mrService,
			FundingOpportunityService foService, MessageSource msgSource) {
		this.fcService = fcService;
		this.fyService = fyService;
		this.mrService = mrService;
		this.foService = foService;
		this.msgSource = msgSource;
	}

	@GetMapping("/browse/viewFCsForFY")
	public String viewFundingCyclesFromFiscalYear(@RequestParam("fyId") Long fyId, Model model) {
		model.addAttribute("fcProjections", fcService.findFundingCyclesByFiscalYearId(fyId));
		return "browse/viewFundingCyclesForFiscalYear";
	}

	@GetMapping("/browse/viewCalendar")
	public String viewCalendar(@RequestParam(name = "plusMinusMonth", defaultValue = "0") Long plusMinusMonth, Model model) {
		model.addAttribute("plusMonth", plusMinusMonth + 1);
		model.addAttribute("minusMonth", plusMinusMonth - 1);
		model.addAttribute("calGrid", new CalendarGrid(plusMinusMonth));

		List<FundingCycleProjection> fcProjections = fcService.findFundingCyclesForCalendar(plusMinusMonth);

		LocalDate startDisplayRange = LocalDate.now().plusMonths(plusMinusMonth).withDayOfMonth(1).minusDays(9);
		LocalDate endDisplayRange = LocalDate.now().plusMonths(plusMinusMonth).withDayOfMonth(28).plusDays(14);

		model.addAttribute("startDates", fcProjections.stream()
				.filter(fc -> fc.getStartDate().isAfter(startDisplayRange) && fc.getStartDate().isBefore(endDisplayRange))
				.sorted(Comparator.comparing(FundingCycleProjection::getStartDate)).collect(Collectors.toList()));
		model.addAttribute("endDates",
				fcProjections.stream()
						.filter(fc -> fc.getEndDate() != null && fc.getEndDate().isAfter(startDisplayRange)
								&& fc.getEndDate().isBefore(endDisplayRange))
						.sorted(Comparator.comparing(FundingCycleProjection::getEndDate)).collect(Collectors.toList()));
		model.addAttribute("startNOIDates", fcProjections.stream()
				.filter(fc -> fc.getStartDateNOI() != null && fc.getStartDateNOI().isAfter(startDisplayRange)
						&& fc.getStartDateNOI().isBefore(endDisplayRange))
				.sorted(Comparator.comparing(FundingCycleProjection::getStartDateNOI)).collect(Collectors.toList()));
		model.addAttribute("endNOIDates", fcProjections.stream()
				.filter(fc -> fc.getEndDateNOI() != null && fc.getEndDateNOI().isAfter(startDisplayRange)
						&& fc.getEndDateNOI().isBefore(endDisplayRange))
				.sorted(Comparator.comparing(FundingCycleProjection::getEndDateNOI)).collect(Collectors.toList()));
		model.addAttribute("startLOIDates", fcProjections.stream()
				.filter(fc -> fc.getStartDateLOI() != null && fc.getStartDateLOI().isAfter(startDisplayRange)
						&& fc.getStartDateLOI().isBefore(endDisplayRange))
				.sorted(Comparator.comparing(FundingCycleProjection::getStartDateLOI)).collect(Collectors.toList()));
		model.addAttribute("endLOIDates",
				fcProjections.stream()
						.filter(fc -> fc.getEndDateLOI() != null && fc.getEndDateLOI().isAfter(startDisplayRange)
								&& fc.getEndDateLOI().isBefore(endDisplayRange))
						.sorted(Comparator.comparing(FundingCycleProjection::getEndDate)).collect(Collectors.toList()));

		return "browse/viewCalendar";
	}

	@GetMapping("/manage/createFC")
	public String createFundingCycleGet(@RequestParam("foId") Long foId, Model model) throws AccessDeniedException {
		if (!mrService.checkIfCurrentUserCanCreateFC(foId)) {
			throw new AccessDeniedException(
					SecurityUtils.getCurrentUsername() + " cannot create a FundingCycle for FundingOpportunity id=" + foId);
		}

		if (!foService.checkIfFundingOpportunityExistsById(foId)) {
			throw new DataRetrievalFailureException(Utility.returnNotFoundMsg("FundingOpportunity", foId));
		}

		model.addAttribute("foId", foId);
		model.addAttribute("fundingCycle", new FundingCycle());
		model.addAttribute("fYrs", fyService.findAllFiscalYearProjectionsOrderByYear());

		return "manage/createFundingCycle";
	}

	@PostMapping("/manage/createFC")
	public String createFundingCyclePost(@RequestParam("foId") Long foId, @Valid @ModelAttribute("fundingCycle") FundingCycle fc,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) throws AccessDeniedException {
		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("fYrs", fyService.findAllFiscalYearProjectionsOrderByYear());
			
			return "manage/createFundingCycle";
		}

		fcService.saveFundingCycle(fc);

		String actionMsg = msgSource.getMessage("h.createdFC", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/manage/manageFo?id=" + foId;
	}

	@GetMapping("/manage/editFC")
	public String editFundingCycleGet(@RequestParam("id") Long fcId, Model model) throws AccessDeniedException {
		if (!mrService.checkIfCurrentUserCanUpdateDeleteFC(fcId)) {
			throw new AccessDeniedException(SecurityUtils.getCurrentUsername() + " cannot update the FundingCycle id=" + fcId);
		}

		model.addAttribute("fc", fcService.findFundingCycleById(fcId));
		model.addAttribute("fYrs", fyService.findAllFiscalYearProjectionsOrderByYear());

		return "manage/editFundingCycle";
	}

	@PostMapping("/manage/editFC")
	public String editFundingCyclePost(@RequestParam("id") Long fcId, @Valid @ModelAttribute("fc") FundingCycle fc,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) throws AccessDeniedException {
		if (bindingResult.hasErrors()) {
			model.addAttribute("fYrs", fyService.findAllFiscalYearProjectionsOrderByYear());
			return "manage/editFundingCycle";
		}

		fcService.saveFundingCycle(fc);

		String actionMsg = msgSource.getMessage("h.editedFC", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/manage/manageFo?id=" + fc.getFundingOpportunity().getId();
	}

	@GetMapping("/manage/deleteFC")
	public String deleteFundingCycle(@RequestParam("id") Long fcId, Model model) throws AccessDeniedException {
		model.addAttribute("fc", fcService.findFundingCycleForConfirmDeleteFC(fcId));

		return "manage/deleteFundingCycle";
	}

	@PostMapping("/manage/deleteFC")
	public String deleteFundingCyclePost(@RequestParam("fcId") Long fcId, @RequestParam("foId") Long foId,
			RedirectAttributes redirectAttributes) throws AccessDeniedException {
		fcService.deleteFundingCycleId(fcId);

		String actionMsg = msgSource.getMessage("h.deletedFC", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/manage/manageFo?id=" + foId;
	}

}
