package com.sentineia.users.perfil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Garante perfis de referência quando a tabela está vazia (antes do utilizador admin).
 */
@Component
@Order(10)
public class PerfilBootstrap implements CommandLineRunner {

    private final PerfilRepository perfilRepository;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    public PerfilBootstrap(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled) {
            return;
        }
        if (perfilRepository.count() > 0) {
            return;
        }
        seed("Administrador", "Acesso total à organização e à matriz de permissões.");
        seed("Triador", "Triagem de denúncias, fila operacional e relatórios operacionais.");
        seed("Investigador", "Tratamento de casos atribuídos e registo de atos.");
        seed("Leitura", "Consulta de denúncias e relatórios permitidos, sem edição.");
    }

    private void seed(String name, String description) {
        Perfil p = new Perfil();
        p.setName(name);
        p.setDescription(description);
        perfilRepository.save(p);
    }
}
