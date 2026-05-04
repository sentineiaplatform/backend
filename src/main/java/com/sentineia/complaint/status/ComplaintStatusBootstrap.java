package com.sentineia.complaint.status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria os status de referência quando a tabela está vazia.
 */
@Component
@Order(11)
public class ComplaintStatusBootstrap implements CommandLineRunner {

    private final ComplaintStatusRepository statusRepository;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    public ComplaintStatusBootstrap(ComplaintStatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled || statusRepository.count() > 0) {
            return;
        }
        seed("Aberta",      "Registro recebido e aguardando triagem.");
        seed("Em análise",  "Em tratamento pela equipe responsável.");
        seed("Pendente",    "Aguardando informação adicional ou retorno externo.");
        seed("Encerrada",   "Processo finalizado com registro de conclusão.");
        seed("Arquivada",   "Arquivamento administrativo sem conclusão formal.");
    }

    private void seed(String name, String description) {
        ComplaintStatus s = new ComplaintStatus();
        s.setName(name);
        s.setDescription(description);
        statusRepository.save(s);
    }
}
