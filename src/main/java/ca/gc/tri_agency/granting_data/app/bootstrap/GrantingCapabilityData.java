package ca.gc.tri_agency.granting_data.app.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({ "local", "test" })
public class GrantingCapabilityData implements CommandLineRunner {

//	@Autowired
//	private GrantingCapabilityService gcService;
//	@Autowired
//	private FundingOpportunityRepository foRepo;
//	@Autowired
//	private GrantingSystemService gSystemService;
//	@Autowired
//	private GrantingStageService gStageService;
//
//	private static final Logger LOG = LoggerFactory.getLogger(GrantingCapabilityData.class);

	@Override
	public void run(String... args) throws Exception {
		// populateWithDesiredStructure();
		// LOG.info("Populated GRANTING_CAPABILITY table");
	}
	/*
	 * private void populateWithDesiredStructure() { List<GrantingSystem>
	 * grantingSystemList = gSystemService.findAll(); Map<String,
	 * GrantingSystem> grantingSystemMap = new HashMap<>(); grantingSystemList
	 * .forEach(grantingSystem -> grantingSystemMap.put(grantingSystem.getAcronym(),
	 * grantingSystem));
	 * 
	 * List<GrantingStage> grantingStageList = new ArrayList<>(); grantingStageList
	 * = gStageService.findAll();
	 * 
	 * List<FundingOpportunity> foList = foRepo.findAll();
	 * 
	 * // APPLY stage entries GrantingStage grantingStageApply = null; for
	 * (GrantingStage grantingStage : grantingStageList) { if
	 * (grantingStage.getNameEn().equals("APPLY")) { grantingStageApply =
	 * grantingStage; break; } } for (FundingOpportunity fo : foList) {
	 * GrantingCapability grantingCapability = new GrantingCapability();
	 * grantingCapability.setGrantingStage(grantingStageApply);
	 * grantingCapability.setFundingOpportunity(fo); String applyMethod =
	 * fo.getApplyMethod(); if (null != fo.getDivision() &&
	 * fo.getDivision().equals("MCT")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym(
	 * "ResearchNet")); gcService.save(grantingCapability); continue; }
	 * else if (null != fo.getProgramLeadName() &&
	 * fo.getProgramLeadName().equals("Open")) {
	 * grantingCapability.setGrantingSystem(gSystemService.
	 * findByAcronym("NSERC Online"));
	 * gcService.save(grantingCapability); } else if ((applyMethod ==
	 * null || applyMethod.equals("Offline") || applyMethod.equals("Not Applicable")
	 * || applyMethod.equals("Email")) && null != fo.getAwardManagementSystem() &&
	 * !fo.getAwardManagementSystem().equals("null") &&
	 * !fo.getAwardManagementSystem().equals("Apears in NAMIS aka CSYN") &&
	 * !fo.getAwardManagementSystem().equals("Apears in NAMIS aka SYN")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym(fo.
	 * getAwardManagementSystem()));
	 * gcService.save(grantingCapability); continue; } else if (null !=
	 * fo.getProgramLeadName() && fo.getProgramLeadName().equals("Allanah Brown")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym(
	 * "ResearchNet")); gcService.save(grantingCapability); continue; }
	 * else if (null != fo.getNameEn() &&
	 * fo.getNameEn().equals("Subatomic Physics - Individual (SAPIN) (5840)")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("RP1"))
	 * ; gcService.save(grantingCapability); continue; } else if (null
	 * != fo.getNameEn() && fo.getNameEn().
	 * equals("Thematic Resources Support in Mathematics and Statistics (CTRMS)")) {
	 * grantingCapability.setGrantingSystem(gSystemService.
	 * findByAcronym("SP Secure Upload"));
	 * gcService.save(grantingCapability); continue; } else if (null !=
	 * fo.getNameEn() && fo.getNameEn().equals("Research Support Fund (RSF)")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("CIMS")
	 * ); gcService.save(grantingCapability); continue; } else if (null
	 * != fo.getNameEn() && fo.getNameEn().equals("Digging into Data")) {
	 * grantingCapability.setGrantingSystem(gSystemService.
	 * findByAcronym("SP Secure Upload"));
	 * gcService.save(grantingCapability); continue; } else if (null !=
	 * fo.getNameEn() && fo.getNameEn().
	 * equals("Special Initiatives Fund For Research Support And Collaboration")) {
	 * grantingCapability.setGrantingSystem(gSystemService.
	 * findByAcronym("SP Secure Upload"));
	 * gcService.save(grantingCapability); continue; } else if (null !=
	 * fo.getNameEn() && fo.getNameEn().contains("Synergy")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("NAMIS"
	 * )); gcService.save(grantingCapability); continue; }
	 * 
	 * for (Map.Entry<String, GrantingSystem> entry : grantingSystemMap.entrySet())
	 * { if (entry != null && entry.getKey() != null && fo.getApplyMethod() != null
	 * && fo.getApplyMethod().equals(entry.getKey())) {
	 * grantingCapability.setGrantingSystem(entry.getValue());
	 * gcService.save(grantingCapability); break; } else if (null !=
	 * fo.getApplyMethod() && null != assignGrantingSystem(fo.getApplyMethod())) {
	 * grantingCapability.setGrantingSystem(
	 * gSystemService.findByAcronym(assignGrantingSystem(fo.getApplyMethod())));
	 * gcService.save(grantingCapability); break; } } }
	 * 
	 * // AWARD stage entries GrantingStage grantingStageAward = null; for
	 * (GrantingStage grantingStage : grantingStageList) { if
	 * (grantingStage.getNameEn().equals("AWARD")) { grantingStageAward =
	 * grantingStage; break; } } for (FundingOpportunity fo : foList) { if (null !=
	 * fo.getNameEn() && (fo.getNameEn().
	 * equals("Parental Leave - Scholarships and Fellowships through grants (SSHRC)"
	 * ) || fo.getNameEn().equals("Parental Leave - Research Grants") ||
	 * fo.getNameEn().equals("Parental Leave - Scholarships & Fellowships"))) {
	 * continue; }
	 * 
	 * GrantingCapability grantingCapability = new GrantingCapability();
	 * grantingCapability.setGrantingStage(grantingStageAward);
	 * grantingCapability.setFundingOpportunity(fo);
	 * 
	 * if (null != fo.getDivision() && fo.getDivision().equals("MCT")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("NAMIS"
	 * )); gcService.save(grantingCapability); continue; } else if
	 * (null != fo.getProgramLeadName() && fo.getProgramLeadName().equals("Open")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("NAMIS"
	 * )); gcService.save(grantingCapability); } else if (null !=
	 * fo.getAwardManagementSystem() &&
	 * fo.getAwardManagementSystem().equals("NAMIS/AMIS/CIHR System")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("NAMIS"
	 * )); gcService.save(grantingCapability);
	 * 
	 * // Spring won't create a new entry in the granting_capability table if I only
	 * // change the Granting System for the GrantingCapability object therefore I
	 * have // to create a new GrantingCapability object.
	 * 
	 * grantingCapability = new GrantingCapability();
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("AMIS")
	 * ); grantingCapability.setGrantingStage(grantingStageAward);
	 * grantingCapability.setFundingOpportunity(fo);
	 * gcService.save(grantingCapability); grantingCapability = new
	 * GrantingCapability();
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym(
	 * "ResearchNet")); grantingCapability.setGrantingStage(grantingStageAward);
	 * grantingCapability.setFundingOpportunity(fo);
	 * gcService.save(grantingCapability); continue; } else if
	 * ((fo.getAwardManagementSystem() == null ||
	 * fo.getAwardManagementSystem().equals("Via Research Offices") ||
	 * fo.getAwardManagementSystem().equals("Partner")) && fo.getApplyMethod() !=
	 * null && !fo.getApplyMethod().equals("Offline") &&
	 * !fo.getApplyMethod().equals("NotApplicable") &&
	 * !fo.getApplyMethod().equals("offline") &&
	 * !fo.getApplyMethod().equals("CIMS/Extranet(SP)")) { grantingCapability
	 * .setGrantingSystem(gSystemService.findByAcronym(assignGrantingSystem(fo.
	 * getApplyMethod()))); gcService.save(grantingCapability);
	 * continue; } else if (null != fo.getProgramLeadName() &&
	 * fo.getProgramLeadName().equals("Allanah Brown")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym(
	 * "ResearchNet")); gcService.save(grantingCapability); continue; }
	 * else if (null != fo.getNameEn() &&
	 * fo.getNameEn().equals("Subatomic Physics - Individual (SAPIN) (5840)")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("RP1"))
	 * ; gcService.save(grantingCapability); continue; } else if (null
	 * != fo.getNameEn() && fo.getNameEn().
	 * equals("Thematic Resources Support in Mathematics and Statistics (CTRMS)")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("NAMIS"
	 * )); gcService.save(grantingCapability); continue; } else if
	 * (null != fo.getNameEn() &&
	 * fo.getNameEn().equals("Research Support Fund (RSF)")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("CIMS")
	 * ); gcService.save(grantingCapability); continue; } else if (null
	 * != fo.getNameEn() && fo.getNameEn().equals("Digging into Data")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("NAMIS"
	 * )); gcService.save(grantingCapability); continue; } else if
	 * (null != fo.getNameEn() && fo.getNameEn().
	 * equals("Special Initiatives Fund For Research Support And Collaboration")) {
	 * grantingCapability.setGrantingSystem(gSystemService.findByAcronym("AMIS")
	 * ); gcService.save(grantingCapability); continue; }
	 * 
	 * for (Map.Entry<String, GrantingSystem> entry : grantingSystemMap.entrySet())
	 * { if (entry != null && entry.getKey() != null &&
	 * fo.getAwardManagementSystem() != null &&
	 * fo.getAwardManagementSystem().equals(entry.getKey())) {
	 * grantingCapability.setGrantingSystem(entry.getValue());
	 * gcService.save(grantingCapability); break; } else if (null !=
	 * fo.getAwardManagementSystem() && null !=
	 * assignGrantingSystem(fo.getAwardManagementSystem())) {
	 * grantingCapability.setGrantingSystem(
	 * gSystemService.findByAcronym(assignGrantingSystem(fo.
	 * getAwardManagementSystem())));
	 * gcService.save(grantingCapability); break; } }
	 * 
	 * } }
	 * 
	 * private static String assignGrantingSystem(String awardOrApplyMethod) {
	 * switch (awardOrApplyMethod) { case "Apears in NAMIS aka CSYN": case
	 * "Apears in NAMIS aka SYN": case "NAMIS": return "NAMIS"; case "NOLS": return
	 * "NSERC Online"; case "SOLS": return "SSHRC Online"; case "RP 1.0": case
	 * "RP1.0": return "RP1"; case "CIMS": case "CIMS/Extranet(SP) ": return "CIMS";
	 * case "ResearchNet": case "Researchnet": case "ResearchNet (CIHR)": return
	 * "ResearchNet"; case "CRM": return "CRM"; case "Secure Upload": case
	 * "Extranet(SP)": return "SP Secure Upload"; case "RP2": case
	 * "RP 2.0 (New system being developed)": return "Convergence"; default: return
	 * null; } }
	 */
}
