package com.sentineia.users.user;

import com.sentineia.users.perfil.Perfil;
import com.sentineia.users.perfil.PerfilRepository;

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
    private final PerfilRepository perfilRepository;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    @Value("${sentineia.bootstrap.admin.email}")
    private String adminEmail;

    @Value("${sentineia.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${sentineia.bootstrap.admin.name}")
    private String adminName;

    public AdminUserBootstrap(
            UserRepository userRepository, UserService userService, PerfilRepository perfilRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.perfilRepository = perfilRepository;
    }

    @Override
    public void run(String... args) {
        if (!bootstrapEnabled) {
            return;
        }
        if (userRepository.count() > 0) {
            return;
        }
        Perfil administrador =
                perfilRepository.findByName("Administrador").orElseThrow(IllegalStateException::new);
        User u = new User();
        u.setName(adminName);
        u.setEmail(adminEmail.trim().toLowerCase());
        u.setPassword(adminPassword);
        u.setPerfil(administrador);
        userService.save(u);
    }
}
