package co.id.abcd.secure.controller;

import co.id.abcd.secure.model.Permission;
import co.id.abcd.secure.service.PermissionService;
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

@Controller
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public String listPermissions(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String search,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Permission> permissionPage = (search != null && !search.isEmpty())
            ? permissionService.searchPermissions(search, pageable)
            : permissionService.getAllPermissions(pageable);

        model.addAttribute("permissions", permissionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", permissionPage.getTotalPages());
        model.addAttribute("totalItems", permissionPage.getTotalElements());
        model.addAttribute("search", search);

        return "permissions/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("permission", new Permission());
        return "permissions/create";
    }

    @PostMapping("/create")
    public String createPermission(@Valid @ModelAttribute("permission") Permission permission,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "permissions/create";
        }

        try {
            permissionService.createPermission(permission);
            redirectAttributes.addFlashAttribute("success", "Permission created successfully");
            return "redirect:/permissions";
        } catch (Exception e) {
            log.error("Failed to create permission", e);
            result.rejectValue("name", "error.permission", e.getMessage());
            return "permissions/create";
        }
    }

    @GetMapping("/{id}")
    public String viewPermission(@PathVariable Long id, Model model) {
        Permission permission = permissionService.getPermissionById(id);
        model.addAttribute("permission", permission);
        return "permissions/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Permission permission = permissionService.getPermissionById(id);
        model.addAttribute("permission", permission);
        return "permissions/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePermission(@PathVariable Long id,
                                   @Valid @ModelAttribute("permission") Permission permission,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "permissions/edit";
        }

        try {
            permissionService.updatePermission(id, permission);
            redirectAttributes.addFlashAttribute("success", "Permission updated successfully");
            return "redirect:/permissions";
        } catch (Exception e) {
            log.error("Failed to update permission", e);
            result.rejectValue("name", "error.permission", e.getMessage());
            return "permissions/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePermission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permissionService.deletePermission(id);
            redirectAttributes.addFlashAttribute("success", "Permission deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete permission", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete permission: " + e.getMessage());
        }
        return "redirect:/permissions";
    }
}
