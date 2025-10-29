package co.id.abcd.secure.service;

import co.id.abcd.secure.model.User;
import co.id.abcd.secure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username)
            .orElseThrow(() -> {
                log.warn("User not found: {}", username);
                return new UsernameNotFoundException("User not found: " + username);
            });

        log.debug("User found: {}, authorities: {}", username, user.getAuthorities());
        return user;
    }
}
