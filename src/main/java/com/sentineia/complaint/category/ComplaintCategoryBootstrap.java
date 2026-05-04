package com.sentineia.complaint.category;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria as categorias de referência quando a tabela está vazia.
 */
@Component
@Order(12)
public class ComplaintCategoryBootstrap implements CommandLineRunner {

    private final ComplaintCategoryRepository categoryRepository;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    public ComplaintCategoryBootstrap(ComplaintCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled || categoryRepository.count() > 0) {
            return;
        }
        seed("Corrupção",               "Desvio de recursos ou conduta potencialmente ilícita.",               30);
        seed("Assédio",                 "Assédio moral, sexual ou discriminação no ambiente de trabalho.",     15);
        seed("Fraude",                  "Falsificação de documentos, adulteração de dados ou registros.",      20);
        seed("Conflito de interesses",  "Situações que possam comprometer a imparcialidade do colaborador.",   20);
        seed("Privacidade / LGPD",      "Tratamento indevido ou vazamento de dados pessoais.",                 10);
        seed("Segurança no trabalho",   "Condições inseguras ou descumprimento de normas de SST.",             15);
        seed("Conduta ética",           "Violação do código de ética ou políticas internas.",                  20);
        seed("Outros",                  "Denúncias que não se enquadram nas categorias acima.",                 30);
    }

    private void seed(String name, String description, int slaDays) {
        ComplaintCategory c = new ComplaintCategory();
        c.setName(name);
        c.setDescription(description);
        c.setSlaDays(slaDays);
        categoryRepository.save(c);
    }
}
