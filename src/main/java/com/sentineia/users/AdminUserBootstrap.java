package com.sentineia.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Cria um utilizador inicial quando a base está vazia (desenvolvimento / primeira subida).
 * Credenciais configuráveis — altere em produção.
 */
@Component
@Order(20)
public class AdminUserBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    @Value("${sentineia.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${sentineia.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${sentineia.bootstrap.admin.name}")
    private String adminName;

    public AdminUserBootstrap(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (!bootstrapEnabled) {
            return;
        }
        if (userRepository.count() > 0) {
            return;
        }
        User u = new User();
        u.setName(adminName);
        u.setEmail(adminEmail.trim().toLowerCase());
        u.setPassword(adminPassword);
        userService.save(u);
    }
}
