package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.Driver;
import hr.algebra.voyabackend.model.Reservation;
import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This service is responsible for sending various notifications during key events.
 * This service should be responsible only for sending emails.
 */
@Service
public class NotificationSenderService {

    private final EmailService emailService;
    private final UserRepository userService;

    public NotificationSenderService(EmailService emailService, UserRepository userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    /**
     * After a new reservation has been added, the booker and all admins are notified.
     * @param reservation Reservation
     */
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

    /**
     * This method notifies new admins and assigned driver (if any) that a reservation is due in 2 hours
     * @param reservation Reservation
     */
    public void sendReservationReminderEmail(Reservation reservation){

        // notify admins
        userService.findByRole(Role.ADMIN).forEach(admin -> {
            String subject = "Reminder: Ride " + reservation.getId() + " is happening soon";
            String text = "Reservation: " + reservation.getPickupLocation() +
                    " to " + reservation.getDropoffLocation() +
                    " is happening soon.";

            emailService.sendEmail(admin.getEmail(), subject, text);
        });

        // notify driver
        Driver driver = reservation.getDriver();
        if (driver != null) {
            String subject = "Reminder: Ride " + reservation.getId() + " is happening soon";
            String text = "Your ride from " + reservation.getPickupLocation() +
                    " to " + reservation.getDropoffLocation() +
                    " is happening soon.";

            emailService.sendEmail(driver.getUser().getEmail(), subject, text);
        }

        // notify passenger
        String subject = "Ride reminder";
        String text = "Your reservation from " + reservation.getPickupLocation() +
                " to " + reservation.getDropoffLocation() +
                " is happening soon.";
        if (driver != null) {
            text = "Your reservation from " + reservation.getPickupLocation() +
                    " to " + reservation.getDropoffLocation() +
                    " is due in 2 hours." +
                    "\n\n" +
                    "Your driver is: " + driver.getUser().getFirstName() + " " + driver.getUser().getLastName();
        }
        emailService.sendEmail(reservation.getUser().getEmail(), subject, text);
    }

    public void sendReservationCancelledEmail(Reservation reservation){
        String subject = "Reservation " +
                reservation.getId() +
                " has been cancelled cancelled";

        String text = "Reservation from " + reservation.getPickupLocation() +
                " to " + reservation.getDropoffLocation() +
                " on " + reservation.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                " has been cancelled.";


        emailService.sendEmail(reservation.getUser().getEmail(), subject, text);

        userService.findByRole(Role.ADMIN).forEach(admin ->
            emailService.sendEmail(admin.getEmail(), subject, text)
        );

        Driver driver = reservation.getDriver();
        if (driver != null) {
            emailService.sendEmail(driver.getUser().getEmail(), subject, text);
        }
    }

    public void notifyDriverOfAssignedReservation(Reservation reservation){
        Driver driver = reservation.getDriver();
        if (driver != null){
            String subject = "New reservation assigned";
            String text = "You have been assigned a new reservation. " +
                    "\n\n" +
                    "From: " + reservation.getPickupLocation() +
                    "\nTo: " + reservation.getDropoffLocation() +
                    "\nFor: " + reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName() +
                    "\nVehicle: " + reservation.getVehicleCategory().getName();

            emailService.sendEmail(driver.getUser().getEmail(), subject, text);
        }

    }
}
