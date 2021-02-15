package ca.gc.tri_agency.granting_data.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ca.gc.tri_agency.granting_data.form.FormErrorCountIterator;
import ca.gc.tri_agency.granting_data.model.FiscalYear;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.FiscalYearService;

@Controller
public class FiscalYearController {

	private FiscalYearService fyService;

	private MessageSource msgSrc;

	@Autowired
	public FiscalYearController(FiscalYearService fyService, MessageSource msgSrc) {
		this.fyService = fyService;
		this.msgSrc = msgSrc;
	}

	@GetMapping("/browse/viewFYs")
	public String viewFiscalYears(Model model) {
		model.addAttribute("fiscalYearStats", fyService.findNumAppsExpectedForEachFiscalYear());
		
		return "browse/viewFiscalYears";
	}

	@AdminOnly
	@GetMapping("/manage/createFY")
	public String createFiscalYearGet(Model model) {
		model.addAttribute("fy", new FiscalYear());
		
		return "manage/createFiscalYear";
	}

	@AdminOnly
	@PostMapping("/manage/createFY")
	public String createFiscalYearPost(@Valid @ModelAttribute("fy") FiscalYear fy, BindingResult bindingResult, Model model) {
		boolean alreadyExists = fyService.checkIfFiscalYearExists(fy);
		if (alreadyExists) {
			bindingResult.addError(
					new FieldError("fy", "year", msgSrc.getMessage("err.yrExists", new Object[] {fy.getYear().toString()}, LocaleContextHolder.getLocale())));
			model.addAttribute("fy", fy);
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));

			return "manage/createFiscalYear";
		}

		fyService.saveFiscalYear(fy);

		return "redirect:/browse/viewFYs";
	}

}
