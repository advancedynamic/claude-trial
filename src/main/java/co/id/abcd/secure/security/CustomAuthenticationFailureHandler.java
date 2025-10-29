package co.id.abcd.secure.security;

import co.id.abcd.secure.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        log.warn("Failed login attempt for user: {}", username);

        // Record failed login attempt
        if (username != null && !username.isEmpty()) {
            userService.recordFailedLogin(username);
        }

        setDefaultFailureUrl("/auth/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
