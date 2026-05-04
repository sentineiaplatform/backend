package com.sentineia.complaint.department;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria os departamentos de referência quando a tabela está vazia.
 */
@Component
@Order(16)
public class ComplaintDepartmentBootstrap implements CommandLineRunner {

    private final ComplaintDepartmentRepository departmentRepository;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    public ComplaintDepartmentBootstrap(ComplaintDepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled || departmentRepository.count() > 0) {
            return;
        }
        seed("Compliance & integridade", "Equipa responsável pela triagem e tratamento de casos de compliance.");
        seed("Recursos humanos", "Tratamento de casos relacionados com conduta de colaboradores.");
        seed("Tecnologia e dados", "Casos relativos a segurança da informação e privacidade.");
        seed("Jurídico institucional", "Casos com implicações legais ou regulatórias.");
        seed("Gestão de risco", "Análise e tratamento de riscos operacionais e reputacionais.");
    }

    private void seed(String name, String description) {
        ComplaintDepartment d = new ComplaintDepartment();
        d.setName(name);
        d.setDescription(description);
        departmentRepository.save(d);
    }
}
