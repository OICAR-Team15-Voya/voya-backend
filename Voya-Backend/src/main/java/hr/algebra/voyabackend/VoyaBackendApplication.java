package hr.algebra.voyabackend;

import hr.algebra.voyabackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class VoyaBackendApplication {

    @Autowired
    private EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(VoyaBackendApplication.class, args);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void sendEmail() {
        emailService.sendEmail("bruno.bogdan02@gmail.com", "", "This is a test email");
    }
}
