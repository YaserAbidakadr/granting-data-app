package ca.gc.tri_agency.granting_data.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import ca.gc.tri_agency.granting_data.model.MemberRole;
import ca.gc.tri_agency.granting_data.model.auditing.UsernameRevisionEntity;
import ca.gc.tri_agency.granting_data.model.projection.MemberRoleProjection;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.MemberRoleRepository;
import ca.gc.tri_agency.granting_data.security.SecurityUtils;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.AuditService;
import ca.gc.tri_agency.granting_data.service.MemberRoleService;

@Service
public class MemberRoleServiceImpl implements MemberRoleService {

	private static final String ENTITY_TYPE = "MemberRole";

	private MemberRoleRepository mrRepo;

	private AuditService auditService;

	@Autowired
	public MemberRoleServiceImpl(MemberRoleRepository memberRoleRepo, AuditService auditService) {
		this.mrRepo = memberRoleRepo;
		this.auditService = auditService;
	}

	@Override
	public MemberRole findMemberRoleById(Long id) {
		return mrRepo.findById(id).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, id)));
	}

	@Override
	public List<MemberRole> findAllMemberRoles() {
		return mrRepo.findAll();
	}

	@Override
	@AdminOnly
	public MemberRole saveMemberRole(MemberRole memberRole) {
		return mrRepo.save(memberRole);
	}

	@Override
	@AdminOnly
	public void deleteMemberRole(Long id) {
		mrRepo.deleteById(id);
	}

	@Override
	public List<MemberRole> findMemberRolesByBusinessUnitId(Long buId) {
		return mrRepo.findByBusinessUnitIdOrderByUserLogin(buId);
	}

	private List<String[]> convertAuditResults(List<Object[]> revisionList) {
		List<String[]> auditedArrList = new ArrayList<>();

		revisionList.forEach(rev -> {
			MemberRole mr = (MemberRole) rev[0];
			UsernameRevisionEntity revEntity = (UsernameRevisionEntity) rev[1];
			RevisionType revType = (RevisionType) rev[2];

			auditedArrList.add(new String[] { mr.getId().toString(), revEntity.getUsername(), revType.toString(), mr.getUserLogin(),
					(mr.getEdiAuthorized() != null) ? mr.getEdiAuthorized().toString().toUpperCase() : null,
					(mr.getRole() != null) ? mr.getRole().getId().toString() : null,
					(mr.getBusinessUnit() != null) ? mr.getBusinessUnit().getId().toString() : null,
					revEntity.getRevTimestamp().toString() });
		});

		return auditedArrList;
	}

	@AdminOnly
	@Override
	public List<String[]> findMemberRoleRevisionsById(Long mrId) {
		List<String[]> resultSet =  convertAuditResults(auditService.findRevisionsForOneMR(mrId));

		if (resultSet.isEmpty()) {
			throw new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, mrId));
		}
		
		return resultSet;
	}

	@AdminOnly
	@Override
	public List<String[]> findAllMemberRoleRevisions() {
		return convertAuditResults(auditService.findRevisionsForAllMRs());
	}

	@Override
	public List<MemberRole> findMRsByUserLoginAndEdiAuthorizedTrue(String userLogin) {
		return mrRepo.findByUserLoginAndEdiAuthorizedTrue(userLogin);
	}

	@Override
	public boolean checkIfCurrentUserEdiAuthorized(Long buId) {
		if (SecurityUtils.isCurrentUserAdmin()) {
			return true;
		}

		MemberRoleProjection mrProjection = mrRepo.findEdiAuthorizedByUserLoginBuId(SecurityUtils.getCurrentUsername(), buId);

		return mrProjection != null ? mrProjection.getEdiAuthorized() : false;
	}

	@Override
	public boolean checkIfCurrentUserCanCreateFC(Long foId) {
		if (SecurityUtils.isCurrentUserAdmin()) {
			return true;
		}

		return mrRepo.findIfCanCreateFC(SecurityUtils.getCurrentUsername(), foId) != null;
	}

	@Override
	public boolean checkIfCurrentUserCanUpdateDeleteFC(Long fcId) {
		if (SecurityUtils.isCurrentUserAdmin()) {
			return true;
		}

		return mrRepo.findIfCanUpdateDeleteFC(SecurityUtils.getCurrentUsername(), fcId) != null;
	}

	@Override
	public MemberRoleProjection findMemberRoleBusinessUnitAcronymRoleName(Long mrId) {
		MemberRoleProjection mr = mrRepo.findUserLoginBusinessUnitNameRoleName(mrId);
		
		if (mr == null) {
			throw new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, mrId));
		}
		
		return mr;
	}

}
