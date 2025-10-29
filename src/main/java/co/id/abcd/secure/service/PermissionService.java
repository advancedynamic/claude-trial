package co.id.abcd.secure.service;

import co.id.abcd.secure.model.Permission;
import co.id.abcd.secure.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Permission createPermission(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new RuntimeException("Permission name already exists");
        }

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created: {}", savedPermission.getName());
        return savedPermission;
    }

    public Permission updatePermission(Long id, Permission updatedPermission) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Permission not found"));

        // Check if name is being changed and if it already exists
        if (!permission.getName().equals(updatedPermission.getName()) &&
            permissionRepository.existsByName(updatedPermission.getName())) {
            throw new RuntimeException("Permission name already exists");
        }

        permission.setName(updatedPermission.getName());
        permission.setDescription(updatedPermission.getDescription());
        permission.setActive(updatedPermission.getActive());

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission updated: {}", savedPermission.getName());
        return savedPermission;
    }

    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (!permission.getRoles().isEmpty()) {
            throw new RuntimeException("Cannot delete permission that is assigned to roles");
        }

        permissionRepository.delete(permission);
        log.info("Permission deleted: {}", permission.getName());
    }

    @Transactional(readOnly = true)
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Transactional(readOnly = true)
    public Page<Permission> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Permission> searchPermissions(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return permissionRepository.findAll(pageable);
        }
        return permissionRepository.searchPermissions(keyword.trim(), pageable);
    }
}
