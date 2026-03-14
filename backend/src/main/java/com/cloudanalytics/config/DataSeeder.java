package com.cloudanalytics.config;

import com.cloudanalytics.entity.User;
import com.cloudanalytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEMO_PASSWORD = "Demo1234";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureUser("admin@demo.com", "Admin", "User", "demo", User.Role.ADMIN);
        ensureUser("analyst@demo.com", "Demo", "Analyst", "demo", User.Role.ANALYST);
        log.info("Demo users ready (admin@demo.com / analyst@demo.com, password: {})", DEMO_PASSWORD);
    }

    private void ensureUser(String email, String firstName, String lastName,
                            String tenantId, User.Role role) {
        userRepository.findByEmail(email).ifPresentOrElse(
            user -> {
                // Always reset to known-good password on startup
                user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
                userRepository.save(user);
                log.debug("Updated password for {}", email);
            },
            () -> {
                User user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(DEMO_PASSWORD))
                        .firstName(firstName)
                        .lastName(lastName)
                        .tenantId(tenantId)
                        .role(role)
                        .isActive(true)
                        .build();
                userRepository.save(user);
                log.debug("Created user {}", email);
            }
        );
    }
}
