package co.id.abcd.secure.controller;

import co.id.abcd.secure.dto.PasswordResetDto;
import co.id.abcd.secure.dto.PasswordResetRequestDto;
import co.id.abcd.secure.dto.UserRegistrationDto;
import co.id.abcd.secure.model.User;
import co.id.abcd.secure.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Registration failed", e);
            result.rejectValue("username", "error.user", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("resetRequest", new PasswordResetRequestDto());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("resetRequest") PasswordResetRequestDto dto,
                                        BindingResult result,
                                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/forgot-password";
        }

        try {
            userService.requestPasswordReset(dto);
            redirectAttributes.addFlashAttribute("success",
                "If your email exists in our system, you will receive a password reset link shortly.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Password reset request failed", e);
            result.rejectValue("email", "error.resetRequest", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setToken(token);
        model.addAttribute("resetDto", dto);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute("resetDto") PasswordResetDto dto,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/reset-password";
        }

        try {
            userService.resetPassword(dto);
            redirectAttributes.addFlashAttribute("success", "Password reset successful! Please login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Password reset failed", e);
            result.rejectValue("password", "error.resetDto", e.getMessage());
            return "auth/reset-password";
        }
    }

    @GetMapping("/request-magic-link")
    public String showMagicLinkRequestPage(Model model) {
        model.addAttribute("magicLinkRequest", new PasswordResetRequestDto());
        return "auth/request-magic-link";
    }

    @PostMapping("/request-magic-link")
    public String processMagicLinkRequest(@Valid @ModelAttribute("magicLinkRequest") PasswordResetRequestDto dto,
                                          BindingResult result,
                                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/request-magic-link";
        }

        try {
            userService.requestMagicLink(dto.getEmail());
            redirectAttributes.addFlashAttribute("success",
                "If your email exists in our system, you will receive a magic link shortly.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Magic link request failed", e);
            result.rejectValue("email", "error.magicLinkRequest", e.getMessage());
            return "auth/request-magic-link";
        }
    }

    @GetMapping("/magic-link")
    public String processMagicLink(@RequestParam("token") String token,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loginWithMagicLink(token);

            // Manually authenticate the user
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            redirectAttributes.addFlashAttribute("success", "Login successful!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            log.error("Magic link login failed", e);
            redirectAttributes.addFlashAttribute("error", "Invalid or expired magic link");
            return "redirect:/auth/login";
        }
    }
}
