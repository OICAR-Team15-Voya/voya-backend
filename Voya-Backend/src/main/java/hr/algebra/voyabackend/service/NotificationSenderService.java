package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.Reservation;
import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This service is responsible for sending various notifications during key events.
 */
@Service
public class NotificationSenderService {

    private final EmailService emailService;
    private final UserRepository userService;

    public NotificationSenderService(EmailService emailService, UserRepository userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    public void sendNewReservationEmail(Reservation reservation) {

        String booker = reservation.getUser().getUsername();
        String subject = "Reservation confirmation";
        String formattedTime = reservation.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        String text = "Your reservation from " + reservation.getPickupLocation() +
                " to " + reservation.getDropoffLocation() +
                " on " + formattedTime +
                "\n\n has been confirmed. Thank you for choosing us!";

        // email booker
        emailService.sendEmail(booker, subject, text);

        // email admins that a new reservation has been created
        List<User> admins = userService.findByRole(Role.ADMIN);
        String adminSubjectTitle = "New reservation created";
        String adminText =
                "New reservation. "
                        + "\n\n"
                        + formattedTime +
                        "\nFrom: " + reservation.getPickupLocation() +
                        "\nTo: " + reservation.getDropoffLocation() +
                        "\nFor: " + reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName() +
                        "\nVehicle: " + reservation.getVehicleCategory().getName();

        for (User admin : admins) {
            emailService.sendEmail(admin.getEmail(), adminSubjectTitle, adminText);
        }
    }
}
