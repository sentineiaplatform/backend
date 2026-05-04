package com.sentineia.complaint.priority;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria as prioridades de referência (P1, P2, P3) quando a tabela está vazia.
 */
@Component
@Order(15)
public class ComplaintPriorityBootstrap implements CommandLineRunner {

    private final ComplaintPriorityRepository priorityRepository;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    public ComplaintPriorityBootstrap(ComplaintPriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled || priorityRepository.count() > 0) {
            return;
        }
        seed("P1", "Urgente", "Caso crítico — requer triagem imediata (até 24 h).");
        seed("P2", "Média", "Caso relevante — prazo padrão de triagem.");
        seed("P3", "Rotina", "Caso de baixa urgência — fila normal de análise.");
    }

    private void seed(String code, String name, String description) {
        ComplaintPriority p = new ComplaintPriority();
        p.setCode(code);
        p.setName(name);
        p.setDescription(description);
        priorityRepository.save(p);
    }
}
