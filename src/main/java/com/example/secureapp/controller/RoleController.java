package com.example.secureapp.controller;

import com.example.secureapp.model.Permission;
import com.example.secureapp.model.Role;
import com.example.secureapp.repository.PermissionRepository;
import com.example.secureapp.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    @GetMapping
    public String listRoles(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Role> rolePage = (search != null && !search.isEmpty())
            ? roleService.searchRoles(search, pageable)
            : roleService.getAllRoles(pageable);

        model.addAttribute("roles", rolePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rolePage.getTotalPages());
        model.addAttribute("totalItems", rolePage.getTotalElements());
        model.addAttribute("search", search);

        return "roles/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("allPermissions", permissionRepository.findAll());
        return "roles/create";
    }

    @PostMapping("/create")
    public String createRole(@Valid @ModelAttribute("role") Role role,
                             BindingResult result,
                             @RequestParam(required = false) Set<Long> permissionIds,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allPermissions", permissionRepository.findAll());
            return "roles/create";
        }

        try {
            roleService.createRole(role, permissionIds);
            redirectAttributes.addFlashAttribute("success", "Role created successfully");
            return "redirect:/roles";
        } catch (Exception e) {
            log.error("Failed to create role", e);
            result.rejectValue("name", "error.role", e.getMessage());
            model.addAttribute("allPermissions", permissionRepository.findAll());
            return "roles/create";
        }
    }

    @GetMapping("/{id}")
    public String viewRole(@PathVariable Long id, Model model) {
        Role role = roleService.getRoleById(id);
        model.addAttribute("role", role);
        return "roles/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Role role = roleService.getRoleById(id);
        List<Permission> allPermissions = permissionRepository.findAll();

        model.addAttribute("role", role);
        model.addAttribute("allPermissions", allPermissions);
        model.addAttribute("rolePermissionIds", role.getPermissions().stream()
            .map(Permission::getId)
            .toList());

        return "roles/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateRole(@PathVariable Long id,
                             @Valid @ModelAttribute("role") Role role,
                             BindingResult result,
                             @RequestParam(required = false) Set<Long> permissionIds,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allPermissions", permissionRepository.findAll());
            return "roles/edit";
        }

        try {
            roleService.updateRole(id, role, permissionIds);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully");
            return "redirect:/roles";
        } catch (Exception e) {
            log.error("Failed to update role", e);
            result.rejectValue("name", "error.role", e.getMessage());
            model.addAttribute("allPermissions", permissionRepository.findAll());
            return "roles/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete role", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete role: " + e.getMessage());
        }
        return "redirect:/roles";
    }
}
