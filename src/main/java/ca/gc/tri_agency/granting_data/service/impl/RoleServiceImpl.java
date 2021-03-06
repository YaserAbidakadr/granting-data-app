package ca.gc.tri_agency.granting_data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import ca.gc.tri_agency.granting_data.model.Role;
import ca.gc.tri_agency.granting_data.model.util.Utility;
import ca.gc.tri_agency.granting_data.repo.RoleRepository;
import ca.gc.tri_agency.granting_data.security.annotations.AdminOnly;
import ca.gc.tri_agency.granting_data.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	
	private static final String ENTITY_TYPE = "Role";

	private RoleRepository roleRepo;

	@Autowired
	public RoleServiceImpl(RoleRepository roleRepo) {
		this.roleRepo = roleRepo;
	}

	@Override
	public Role findRoleById(Long id) {
		return roleRepo.findById(id).orElseThrow(() -> new DataRetrievalFailureException(Utility.returnNotFoundMsg(ENTITY_TYPE, id)));
	}

	@Override
	public List<Role> findAllRoles() {
		return roleRepo.findAll();
	}

	@AdminOnly
	@Override
	public Role saveRole(Role role) {
		return roleRepo.save(role);
	}

}
