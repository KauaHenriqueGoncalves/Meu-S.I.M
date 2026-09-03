package com.meusim.application.integration.email.listener;

import com.meusim.application.integration.email.dto.SendEmailSubscriptionPaid;
import com.meusim.application.integration.email.service.EmailSendService;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.licensing.schoolsubscription.dto.SchoolSubscriptionDetailResponse;
import com.meusim.application.modules.licensing.schoolsubscription.event.SubscriptionPaidEmailToAdminsEvent;
import com.meusim.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.meusim.application.modules.school.query.SchoolAdminQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class SubscriptionListener {
    private final static Logger log =
            LoggerFactory.getLogger(SubscriptionListener.class);

    private final SchoolSubscriptionService subscriptionService;
    private final SchoolAdminQuery schoolAdminQuery;
    private final EmailSendService emailSendService;

    public SubscriptionListener(
            SchoolSubscriptionService subscriptionService,
            SchoolAdminQuery schoolAdminQuery,
            EmailSendService emailSendService
    ) {
        this.subscriptionService = subscriptionService;
        this.schoolAdminQuery = schoolAdminQuery;
        this.emailSendService = emailSendService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlerSubscriptionPaidAdminsEmail(SubscriptionPaidEmailToAdminsEvent event) {
        List<SchoolAdmin> admins = schoolAdminQuery.findAllBySchoolId(event.schoolId());
        if (admins.isEmpty()) {
            log.warn("O evento para o envio de email da confirmação de pagamento da licença está sem email. [subscriptionId={}] [schoolId={}]",
                    event.subscriptionId(), event.schoolId());
            return;
        }
        List<String> emails = admins.stream().map(a -> a.getUser().getEmail()).toList();
        SchoolSubscriptionDetailResponse detail = subscriptionService.findDetailById(event.subscriptionId());
        SendEmailSubscriptionPaid info = SendEmailSubscriptionPaid.from(detail);
        log.info("Evento de enviar a licença paga para todos os admins do reforço. [schoolId={}]", event.schoolId());
        emailSendService.sendSubscriptionPaidEmails(emails, event.schoolId(), info);
        log.info("E-mails enviados com sucesso para os admins. [emails={}] [subscriptionId={}] [schooId={}]",
                emails, event.subscriptionId(), event.schoolId());
    }
}
