package com.sentineia.complaint.complaint;

import com.sentineia.complaint.category.ComplaintCategory;
import com.sentineia.complaint.category.ComplaintCategoryRepository;
import com.sentineia.complaint.department.ComplaintDepartment;
import com.sentineia.complaint.department.ComplaintDepartmentRepository;
import com.sentineia.complaint.priority.ComplaintPriority;
import com.sentineia.complaint.priority.ComplaintPriorityRepository;
import com.sentineia.complaint.status.ComplaintStatus;
import com.sentineia.complaint.status.ComplaintStatusRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Cria denúncias de demonstração quando a tabela está vazia.
 * Depende dos bootstraps de status (11), categoria (12), prioridade (15) e departamento (16).
 */
@Component
@Order(20)
public class ComplaintBootstrap implements CommandLineRunner {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCategoryRepository categoryRepository;
    private final ComplaintStatusRepository statusRepository;
    private final ComplaintPriorityRepository priorityRepository;
    private final ComplaintDepartmentRepository departmentRepository;
    private final ComplaintService complaintService;

    @Value("${sentineia.bootstrap.enabled}")
    private boolean bootstrapEnabled;

    public ComplaintBootstrap(
            ComplaintRepository complaintRepository,
            ComplaintCategoryRepository categoryRepository,
            ComplaintStatusRepository statusRepository,
            ComplaintPriorityRepository priorityRepository,
            ComplaintDepartmentRepository departmentRepository,
            ComplaintService complaintService) {
        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.departmentRepository = departmentRepository;
        this.complaintService = complaintService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapEnabled || complaintRepository.count() > 0) {
            return;
        }

        List<ComplaintStatus> statuses = statusRepository.findAll();
        List<ComplaintCategory> categories = categoryRepository.findAll();
        List<ComplaintPriority> priorities = priorityRepository.findAll();
        List<ComplaintDepartment> departments = departmentRepository.findAll();

        if (statuses.isEmpty() || categories.isEmpty() || priorities.isEmpty() || departments.isEmpty()) {
            return;
        }

        record Row(String title, String description, String channel, boolean anonymous,
                   int statusIdx, int categoryIdx, int priorityIdx, int departmentIdx) {}

        List<Row> rows = List.of(
            new Row("Assédio moral em reuniões semanais de equipa",
                """
                Trabalho nesta empresa há três anos e desde janeiro deste ano venho sendo alvo de comentários depreciativos por parte da minha chefia direta durante as reuniões semanais de acompanhamento, realizadas às segundas-feiras, geralmente com clientes presentes na sala.

                As situações mais graves ocorreram em 14 de janeiro, 4 de fevereiro e 3 de março. Em todas essas ocasiões o gestor referiu-se ao meu trabalho como "amadorismo", "incompetência recorrente" e chegou a dizer, na presença de um cliente da área comercial, que "qualquer estagiário faria melhor". Outros colegas presentes podem confirmar — pelo menos dois deles manifestaram desconforto em privado.

                Já tentei resolver a situação de forma informal, tendo conversado com o responsável de RH em fevereiro, mas não houve qualquer retorno ou intervenção. O comportamento manteve-se e agravar-se em março.

                Solicito investigação discreta, pois temo represálias na próxima avaliação de desempenho, agendada para abril.""",
                "Canal web", true, 1, 1, 0, 1),

            new Row("Desvio de recursos em processo de compras sem concurso",
                """
                Venho por este meio denunciar irregularidades no processo de aquisição de serviços de consultoria ocorrido entre outubro e dezembro do ano passado, no valor total aproximado de 280.000 euros.

                A empresa contratada — que presto não poder identificar por receio de retaliação — foi selecionada sem abertura de concurso público, em aparente violação do regulamento interno de procurement. O processo foi gerido exclusivamente pelo diretor da área, sem envolvimento da comissão de compras habitual.

                Adicionalmente, foram emitidos três aditamentos ao contrato original, cada um abaixo do limiar de aprovação do conselho, o que sugere que o valor foi fracionado propositadamente para evitar escrutínio. Os pagamentos foram aprovados pelo mesmo diretor que adjudicou o serviço, sem co-assinatura.

                Tenho conhecimento de que dois colaboradores da equipa financeira levantaram dúvidas internamente e foram informalmente aconselhados a não prosseguir com questões.""",
                "Telefone", true, 0, 0, 0, 0),

            new Row("Envio não autorizado de base de dados de clientes para endereço externo",
                """
                No dia 22 do mês passado, ao fazer uma auditoria de rotina nos logs do servidor de correio eletrónico, identifiquei um envio de ficheiro para um endereço Gmail externo que não consta da lista de destinatários autorizados da empresa.

                O ficheiro enviado continha uma exportação completa da base de dados de clientes da região Sul, incluindo nome, NIF, IBAN, endereço e histórico de compras — aproximadamente 14.000 registos. O envio foi feito por um colaborador do departamento de TI com perfil de administrador, fora do horário laboral, às 23h47.

                O colaborador em causa não apresentou qualquer pedido de autorização para exportação de dados nesse período. A política de proteção de dados da empresa proíbe expressamente este tipo de envio sem aprovação do DPO.

                Guarei evidência do log e posso apresentá-la se necessário. Prefiro manter anonimato dada a sensibilidade da minha posição.""",
                "Canal web", false, 1, 4, 1, 2),

            new Row("Conflito de interesses não declarado em adjudicação de contrato",
                """
                É do meu conhecimento que um gestor sénior da área de operações detém participação acionista numa empresa de serviços de limpeza industrial que, no último trimestre, foi contratada por esta organização no valor de 95.000 euros anuais.

                O mesmo gestor fez parte da comissão de avaliação das propostas e, tanto quanto sei, não apresentou qualquer declaração de impedimento ou conflito de interesses no formulário obrigatório que antecede a adjudicação. A empresa em questão não era a proposta mais económica — havia pelo menos outras duas propostas com preços substancialmente inferiores.

                Soube desta situação através de um colega que reconheceu o nome da empresa num documento interno. Após verificação, confirmámos a ligação acionista através de pesquisa no registo comercial público.

                Solicito que esta situação seja verificada com toda a confidencialidade, pois o gestor em causa tem influência direta sobre avaliações de desempenho da nossa equipa.""",
                "E-mail", true, 0, 3, 1, 3),

            new Row("Condições de segurança críticas no armazém do setor logístico",
                """
                Sou colaborador do turno da tarde no armazém e considero necessário reportar formalmente um conjunto de situações que representam risco real para a segurança dos trabalhadores.

                Em primeiro lugar, os equipamentos de proteção individual (capacetes, coletes e luvas) disponíveis são insuficientes para o número de pessoas no turno. No último mês, pelo menos quatro colaboradores trabalharam sem colete refletor, por falta de stock. As requisições para reposição foram feitas em novembro e ainda não foram atendidas.

                Em segundo lugar, a saída de emergência Este está sistematicamente bloqueada com paletes desde meados de dezembro. Já foi reportado ao encarregado, sem resultado.

                Em terceiro, durante uma verificação informal detetei que um dos extintores do corredor B tem data de validade expirada desde setembro. O extintor não foi substituído.

                Estas situações, em conjunto, podem constituir violação das normas de segurança e higiene no trabalho. Solicito inspeção urgente.""",
                "Presencial", false, 1, 5, 2, 1),

            new Row("Adulteração de relatório de inspeção de equipamentos",
                """
                Trabalho na área de manutenção e tenho acesso ao sistema de registo de medições de equipamentos críticos. No início deste mês identifiquei uma discrepância que considero muito grave entre os valores registados no sistema e os valores que constam no relatório de conformidade enviado à entidade reguladora.

                Especificamente, os valores de pressão máxima de operação das caldeiras do setor B, medidos no sistema entre 8 e 12 de fevereiro, estão sistematicamente acima dos limites autorizados. No entanto, o relatório de conformidade enviado a 15 de fevereiro apresenta valores dentro dos parâmetros, o que não corresponde à realidade registada.

                O relatório foi assinado pelo responsável técnico da área. Não sei se a alteração foi feita por ele diretamente ou se houve instruções superiores, mas os dados do sistema são inequívocos.

                Tenho capturas dos ecrãs do sistema com os valores reais. Não me sinto seguro em partilhar internamente por receio de consequências profissionais.""",
                "Canal web", true, 0, 2, 0, 0),

            new Row("Assédio sexual reiterado por responsável de departamento",
                """
                Decidi submeter esta denúncia após meses de hesitação, pois a situação tornou-se insustentável e afeta gravemente o meu bem-estar e a minha capacidade de trabalho.

                Desde setembro do ano passado sou alvo de abordagens de cariz sexual por parte de um responsável de departamento. As situações ocorreram principalmente durante deslocações em serviço e em almoços de equipa. As abordagens incluem comentários sobre a minha aparência física em contexto inapropriado, convites reiterados para encontros fora do horário laboral após recusa clara da minha parte, e uma situação em novembro que envolveu contacto físico não consentido.

                Tenho mensagens de texto e e-mails que documentam parte do comportamento. Uma colega que viajou comigo em outubro presenciou parte de uma das situações.

                Nunca reportei antes por receio do impacto na minha carreira e por não ter a certeza de que seria levada a sério. Solicito tratamento absolutamente confidencial e que o responsável em causa não seja informado de que fui eu a denunciar.""",
                "Canal web", true, 1, 1, 0, 1),

            new Row("Partilha não autorizada de dados sensíveis de colaboradores com terceiros",
                """
                Soube por uma fonte interna de confiança que dados pessoais sensíveis de colaboradores desta empresa — incluindo registos de saúde, histórico de absentismo e dados bancários para efeitos de processamento salarial — foram partilhados com uma empresa de consultoria externa contratada para um projeto de reestruturação organizacional.

                Esta partilha terá ocorrido em janeiro deste ano, sem que os colaboradores tenham sido informados ou dado consentimento explícito. Até onde tenho conhecimento, o contrato com a consultora não previa o tratamento de dados pessoais de colaboradores, e não houve qualquer aditamento ao contrato nem informação ao encarregado de proteção de dados.

                A situação poderá constituir violação grave da LGPD e da política interna de privacidade. Solicito investigação urgente e, se aplicável, notificação à autoridade de controlo competente.""",
                "E-mail", false, 0, 4, 1, 2),

            new Row("Pagamento não contabilizado a inspetor externo para aprovação de auditoria",
                """
                Sou colaborador da área financeira e processámos, no mês passado, um pagamento que me parece altamente irregular e que considero meu dever reportar.

                O pagamento, no valor de 3.800 euros, foi registado como "serviços de consultoria pontual" mas não existe qualquer contrato, proposta ou fatura com descritivo de serviços associado. O beneficiário é uma pessoa singular que, tanto quanto apurei, é inspetor externo acreditado para auditorias do nosso setor de atividade.

                Curiosamente, este pagamento foi processado dois dias antes de recebermos o resultado positivo de uma auditoria de conformidade que, com base em interações anteriores, não era expectável que corresse bem. O processo de auditoria em causa verificou-se em novembro e os resultados foram comunicados em dezembro.

                O pagamento foi autorizado por um diretor sénior e não passou pelos procedimentos normais de aprovação financeira. Tenho cópia do comprovativo de transferência e do registo interno.""",
                "Telefone", true, 1, 0, 0, 3),

            new Row("Discriminação de género em processo de promoção interna",
                """
                No ciclo de promoções de outubro passado, três posições de coordenação foram preenchidas internamente. Conheço bem o processo porque faço parte da equipa e acompanhei de perto as avaliações.

                As duas candidatas internas com as classificações de desempenho mais elevadas nos últimos dois anos — ambas com avaliações de 4.8 e 4.6 em 5 — foram preteridas em favor de três candidatos masculinos com classificações inferiores (4.1, 3.9 e 4.2). Nenhuma das candidatas recebeu feedback detalhado sobre a decisão, apenas uma resposta genérica sobre "adequação ao perfil de liderança".

                Quando uma das colaboradoras pediu reunião para entender os critérios, foi informada verbalmente de que "a empresa considera importantes fatores de disponibilidade e mobilidade" — critérios que não constavam do regulamento de promoção publicado e que podem constituir discriminação indireta.

                Tenho cópia das avaliações de desempenho relevantes, obtidas de forma legítima, que sustentam o que descrevo.""",
                "Canal web", true, 0, 6, 1, 1),

            new Row("Descarga de efluentes industriais fora dos limites regulatórios autorizados",
                """
                Sou responsável pelo sistema de monitorização ambiental desta unidade e venho por este meio alertar formalmente para uma situação de incumprimento sistemático das licenças de descarga que nos foram atribuídas pela autoridade ambiental competente.

                Desde pelo menos outubro do ano passado, os registos internos do sistema de controlo mostram que os parâmetros de carência química de oxigénio (CQO) e de sólidos suspensos totais (SST) nas descargas do setor de produção C excedem regularmente os limites máximos autorizados — em alguns casos em mais de 40%.

                Os relatórios enviados à autoridade ambiental não refletem estes valores reais. Fui informado, por um superior hierárquico, que os relatórios são "ajustados" antes de ser enviados para "não criar problemas desnecessários". Recusei participar nesta prática e temo agora represálias.

                Esta situação constitui, no meu entender, uma violação grave da lei ambiental e potencialmente um crime ambiental. Disponho de registos do sistema de monitorização que documentam o incumprimento.""",
                "Canal web", false, 2, 6, 2, 4),

            new Row("Partilha de proposta confidencial de concorrente com parceiro estratégico",
                """
                No decorrer de uma reunião interna de preparação para uma negociação com um parceiro estratégico, a que assisti como observador, apercebi-me de que o responsável comercial partilhou abertamente detalhes de uma proposta que, pelo contexto da conversa, só poderia ter origem num concorrente direto.

                Os detalhes incluíam estrutura de preços, condições de pagamento e elementos técnicos específicos que não são informação pública. Quando questionei informalmente como tinha tido acesso àquela informação, recebi uma resposta evasiva sobre "fontes do mercado".

                A proposta do concorrente em causa acabou por ser excluída da negociação de forma algo abrupta, o que reforça a minha suspeita de que a informação obtida indevidamente foi usada para influenciar o processo negocial a nosso favor.

                Não sei se esta situação envolveu espionagem industrial, receção de informação confidencial de um insider do concorrente, ou outra forma de obtenção ilegal. Considero que deve ser investigada, independentemente do resultado comercial favorável.""",
                "E-mail", true, 0, 6, 1, 0)
        );

        for (Row r : rows) {
            ComplaintStatus status = statuses.get(r.statusIdx() % statuses.size());
            ComplaintCategory category = categories.get(r.categoryIdx() % categories.size());
            ComplaintPriority priority = priorities.get(r.priorityIdx() % priorities.size());
            ComplaintDepartment department = departments.get(r.departmentIdx() % departments.size());

            Complaint c = new Complaint();
            c.setTitle(r.title());
            c.setDescription(r.description());
            c.setChannel(r.channel());
            c.setAnonymous(r.anonymous());
            c.setStatus(status);
            c.setCategory(category);
            c.setPriority(priority);
            c.setDepartment(department);

            complaintService.save(c);
        }
    }
}
