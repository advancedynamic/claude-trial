package co.id.abcd.secure.controller;

import co.id.abcd.secure.model.Role;
import co.id.abcd.secure.model.User;
import co.id.abcd.secure.repository.RoleRepository;
import co.id.abcd.secure.service.UserService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'USER_MANAGER')")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> userPage = (search != null && !search.isEmpty())
            ? userService.searchUsers(search, pageable)
            : userService.getAllUsers(pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("search", search);

        return "users/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             @RequestParam(required = false) Set<Long> roleIds,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/create";
        }

        try {
            userService.createUser(user, roleIds);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
            return "redirect:/users";
        } catch (Exception e) {
            log.error("Failed to create user", e);
            result.rejectValue("username", "error.user", e.getMessage());
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/create";
        }
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "users/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<Role> allRoles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("userRoleIds", user.getRoles().stream()
            .map(Role::getId)
            .toList());

        return "users/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             @RequestParam(required = false) Set<Long> roleIds,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/edit";
        }

        try {
            userService.updateUser(id, user, roleIds);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
            return "redirect:/users";
        } catch (Exception e) {
            log.error("Failed to update user", e);
            result.rejectValue("username", "error.user", e.getMessage());
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete user", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
