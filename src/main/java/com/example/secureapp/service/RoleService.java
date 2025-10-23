package com.example.secureapp.service;

import com.example.secureapp.model.Permission;
import com.example.secureapp.model.Role;
import com.example.secureapp.repository.PermissionRepository;
import com.example.secureapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role createRole(Role role, Set<Long> permissionIds) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role name already exists");
        }

        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionIds.forEach(permissionId -> {
                Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
                role.addPermission(permission);
            });
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole.getName());
        return savedRole;
    }

    public Role updateRole(Long id, Role updatedRole, Set<Long> permissionIds) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        // Check if name is being changed and if it already exists
        if (!role.getName().equals(updatedRole.getName()) &&
            roleRepository.existsByName(updatedRole.getName())) {
            throw new RuntimeException("Role name already exists");
        }

        role.setName(updatedRole.getName());
        role.setDescription(updatedRole.getDescription());
        role.setActive(updatedRole.getActive());

        // Update permissions
        role.getPermissions().clear();
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionIds.forEach(permissionId -> {
                Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
                role.addPermission(permission);
            });
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role updated: {}", savedRole.getName());
        return savedRole;
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role that is assigned to users");
        }

        roleRepository.delete(role);
        log.info("Role deleted: {}", role.getName());
    }

    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Role> searchRoles(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return roleRepository.findAll(pageable);
        }
        return roleRepository.searchRoles(keyword.trim(), pageable);
    }
}
