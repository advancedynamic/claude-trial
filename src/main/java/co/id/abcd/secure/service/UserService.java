package co.id.abcd.secure.service;

import co.id.abcd.secure.dto.PasswordResetDto;
import co.id.abcd.secure.dto.PasswordResetRequestDto;
import co.id.abcd.secure.dto.UserRegistrationDto;
import co.id.abcd.secure.model.Role;
import co.id.abcd.secure.model.User;
import co.id.abcd.secure.repository.RoleRepository;
import co.id.abcd.secure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = User.builder()
            .username(dto.getUsername())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .phone(dto.getPhone())
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .loginAttempts(0)
            .build();

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        user.addRole(userRole);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    public void requestPasswordReset(PasswordResetRequestDto dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            emailService.sendPasswordResetEmail(user, token);
            log.info("Password reset requested for user: {}", user.getUsername());
        }
        // Always return success to prevent email enumeration
    }

    public void resetPassword(PasswordResetDto dto) {
        User user = userRepository.findByValidPasswordResetToken(dto.getToken(), LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getUsername());
    }

    public void requestMagicLink(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setMagicLinkToken(token);
            user.setMagicLinkTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            emailService.sendMagicLinkEmail(user, token);
            log.info("Magic link requested for user: {}", user.getUsername());
        }
    }

    public User loginWithMagicLink(String token) {
        User user = userRepository.findByValidMagicLinkToken(token, LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("Invalid or expired magic link"));

        user.setMagicLinkToken(null);
        user.setMagicLinkTokenExpiry(null);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLoginAttempts(0);
        userRepository.save(user);

        log.info("User logged in with magic link: {}", user.getUsername());
        return user;
    }

    public User createUser(User user, Set<Long> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> {
                Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
                user.addRole(role);
            });
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser, Set<Long> roleIds) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhone(updatedUser.getPhone());
        user.setEnabled(updatedUser.getEnabled());

        // Update password only if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Update roles
        user.getRoles().clear();
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> {
                Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
                user.addRole(role);
            });
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        log.info("User deleted: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.searchUsers(keyword.trim(), pageable);
    }

    public void recordSuccessfulLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            user.setLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        });
    }

    public void recordFailedLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getLoginAttempts() + 1;
            user.setLoginAttempts(attempts);

            if (attempts >= 5) {
                user.setAccountNonLocked(false);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                log.warn("User account locked due to failed login attempts: {}", username);
            }

            userRepository.save(user);
        });
    }
}
