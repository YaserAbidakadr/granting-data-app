package ca.gc.tri_agency.granting_data.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.gc.tri_agency.granting_data.form.FormErrorCountIterator;
import ca.gc.tri_agency.granting_data.ldap.ADUser;
import ca.gc.tri_agency.granting_data.ldap.ADUserService;
import ca.gc.tri_agency.granting_data.model.MemberRole;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.BusinessUnitService;
import ca.gc.tri_agency.granting_data.service.MemberRoleService;

@AdminOnly
@Controller
public class MemberRoleController {

	private MemberRoleService mrService;

	private BusinessUnitService buService;

	private ADUserService adUserService;

	private MessageSource msgSrc;

	@Autowired
	public MemberRoleController(MemberRoleService memberRoleService, BusinessUnitService buService, ADUserService adUserService,
			MessageSource msgSrc) {
		this.mrService = memberRoleService;
		this.buService = buService;
		this.adUserService = adUserService;
		this.msgSrc = msgSrc;
	}

	@GetMapping("/admin/createMR")
	public String createMemberRoleGet(@RequestParam("buId") Long buId,
			@RequestParam(value = "searchStr", defaultValue = "") String searchStr, Model model) {
		List<ADUser> userList = new ArrayList<>();

		if (!searchStr.trim().isEmpty()) {
			userList = adUserService.searchADUsers(searchStr.trim());
			model.addAttribute("adUserList", userList);
		}

		if (!userList.isEmpty()) {
			MemberRole memberRole = new MemberRole();
			memberRole.setBusinessUnit(buService.findBusinessUnitById(buId));

			model.addAttribute("memberRole", memberRole);
		}

		return "admin/createMemberRole";
	}

	@PostMapping("/admin/createMR")
	public String createMemeberRolePost(@RequestParam("buId") Long buId, @RequestParam("searchStr") String searchStr,
			@Valid @ModelAttribute("memberRole") MemberRole mr, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("topErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("formErrCounter", new FormErrorCountIterator(bindingResult.getFieldErrorCount()));
			model.addAttribute("adUserList", adUserService.searchADUsers(searchStr));
			
			return "admin/createMemberRole";
		}
		
		mr = mrService.saveMemberRole(mr);

		String actionMsg = msgSrc.getMessage("h.createdMR", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg + mr.getUserLogin());

		return "redirect:/browse/viewBU?id=" + buId;
	}

	@GetMapping("/admin/deleteMR")
	public String deleteMemberRoleGet(@RequestParam("id") Long mrId, Model model) {
		model.addAttribute("mrProjection", mrService.findMemberRoleBusinessUnitAcronymRoleName(mrId));
		
		return "admin/deleteMemberRole";
	}
	
	@PostMapping(value = "/admin/deleteMR")
	public String deleteMemberRolePost(@RequestParam("id") Long mrId, RedirectAttributes redirectAttributes) {
		MemberRole mr = mrService.findMemberRoleById(mrId);
		Long buId = mr.getBusinessUnit().getId();

		mrService.deleteMemberRole(mrId);

		String actionMsg = msgSrc.getMessage("h.deletedMR", null, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("actionMsg", actionMsg);

		return "redirect:/browse/viewBU?id=" + buId;
	}

	@GetMapping("/admin/auditLogMR")
	public String memberRoleAuditLog(@RequestParam(value = "id", defaultValue = "0") Long id,
			@RequestHeader(value = "referer", required = false) String referer, Model model) {
		List<String[]> revisionList = (id > 0L) ? mrService.findMemberRoleRevisionsById(id) : mrService.findAllMemberRoleRevisions();
		
		model.addAttribute("revisionList", revisionList);

		return "admin/memberRoleAuditLog";
	}

}
