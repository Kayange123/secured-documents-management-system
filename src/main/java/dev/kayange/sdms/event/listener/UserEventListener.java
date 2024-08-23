package dev.kayange.sdms.event.listener;

import dev.kayange.sdms.enumeration.EmailTemplate;
import dev.kayange.sdms.event.UserEvent;
import dev.kayange.sdms.service.MailService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final MailService mailService;

    @EventListener
    public void onUserEvent(@NotNull UserEvent event){
        switch (event.getType()){
            case REGISTRATION -> mailService.sendMail(event.getUser().getEmail(), event.getUser().getFullName(), EmailTemplate.ACTIVATE_ACCOUNT, (String)event.getData().get("confirmationUrl"), (String)event.getData().get("activationCode"), (String)event.getData().get("subject") );
            case PASSWORD_RESET -> mailService.sendMail(event.getUser().getEmail(), event.getUser().getFullName(), EmailTemplate.RESET_PASSWORD, (String)event.getData().get("confirmationUrl"), (String)event.getData().get("activationCode"), (String)event.getData().get("subject") );
            default -> {}
        }
    }
}
