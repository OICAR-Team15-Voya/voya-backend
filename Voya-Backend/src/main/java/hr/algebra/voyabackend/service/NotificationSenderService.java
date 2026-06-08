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
        String formattedTime = reservation.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        String subject = "Reservation confirmation";
        String text = "Your reservation from " + reservation.getPickupLocation() +
                " to " + reservation.getDropoffLocation() +
                " on " + formattedTime +
                "\n\n has been confirmed. Thank you for choosing us!";

        // email booker
        try {
            emailService.sendEmail(reservation.getUser().getUsername(), subject, text);
        } catch (Exception e) {
            System.err.println("[NotificationService] Failed to send confirmation email to booker: "
                    + reservation.getUser().getEmail() + " | Error: " + e.getMessage());
        }

        // email admins
        String adminSubjectTitle = "New reservation created";
        String adminText = "New reservation. "
                + "\n\n"
                + formattedTime
                + "\nFrom: " + reservation.getPickupLocation()
                + "\nTo: " + reservation.getDropoffLocation()
                + "\nFor: " + reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName()
                + "\nVehicle: " + reservation.getVehicleCategory().getName();

        try {
            List<User> admins = userService.findByRole(Role.ADMIN);
            for (User admin : admins) {
                try {
                    emailService.sendEmail(admin.getEmail(), adminSubjectTitle, adminText);
                } catch (Exception e) {
                    System.err.println("[NotificationService] Failed to send new reservation email to admin: "
                            + admin.getEmail() + " | Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[NotificationService] Failed to fetch admins for new reservation notification | Error: " + e.getMessage());
        }
    }

    /**
     * This method notifies admins, driver and passenger that a reservation is due in 2 hours.
     * @param reservation Reservation
     */
    public void sendReservationReminderEmail(Reservation reservation) {
        Driver driver = reservation.getDriver();

        // notify admins
        try {
            userService.findByRole(Role.ADMIN).forEach(admin -> {
                try {
                    String subject = "Reminder: Ride " + reservation.getId() + " is happening soon";
                    String text = "Reservation: " + reservation.getPickupLocation() +
                            " to " + reservation.getDropoffLocation() +
                            " is happening soon.";
                    emailService.sendEmail(admin.getEmail(), subject, text);
                } catch (Exception e) {
                    System.err.println("[NotificationService] Failed to send reminder to admin: "
                            + admin.getEmail() + " | Error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("[NotificationService] Failed to fetch admins for reminder notification | Error: " + e.getMessage());
        }

        // notify driver
        if (driver != null) {
            try {
                String subject = "Reminder: Ride " + reservation.getId() + " is happening soon";
                String text = "Your ride from " + reservation.getPickupLocation() +
                        " to " + reservation.getDropoffLocation() +
                        " is happening soon.";
                emailService.sendEmail(driver.getUser().getEmail(), subject, text);
            } catch (Exception e) {
                System.err.println("[NotificationService] Failed to send reminder to driver: "
                        + driver.getUser().getEmail() + " | Error: " + e.getMessage());
            }
        }

        // notify passenger
        try {
            String subject = "Ride reminder";
            String text = driver != null
                    ? "Your reservation from " + reservation.getPickupLocation() +
                    " to " + reservation.getDropoffLocation() +
                    " is due in 2 hours." +
                    "\n\nYour driver is: " + driver.getUser().getFirstName() + " " + driver.getUser().getLastName()
                    : "Your reservation from " + reservation.getPickupLocation() +
                    " to " + reservation.getDropoffLocation() +
                    " is happening soon.";
            emailService.sendEmail(reservation.getUser().getEmail(), subject, text);
        } catch (Exception e) {
            System.err.println("[NotificationService] Failed to send reminder to passenger: "
                    + reservation.getUser().getEmail() + " | Error: " + e.getMessage());
        }
    }

    /**
     * Notifies booker, admins and driver that a reservation has been cancelled.
     * @param reservation Reservation
     */
    public void sendReservationCancelledEmail(Reservation reservation) {
        String subject = "Reservation " + reservation.getId() + " has been cancelled";
        String text = "Reservation from " + reservation.getPickupLocation() +
                " to " + reservation.getDropoffLocation() +
                " on " + reservation.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                " has been cancelled.";

        // notify passenger
        try {
            emailService.sendEmail(reservation.getUser().getEmail(), subject, text);
        } catch (Exception e) {
            System.err.println("[NotificationService] Failed to send cancellation email to passenger: "
                    + reservation.getUser().getEmail() + " | Error: " + e.getMessage());
        }

        // notify admins
        try {
            userService.findByRole(Role.ADMIN).forEach(admin -> {
                try {
                    emailService.sendEmail(admin.getEmail(), subject, text);
                } catch (Exception e) {
                    System.err.println("[NotificationService] Failed to send cancellation email to admin: "
                            + admin.getEmail() + " | Error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("[NotificationService] Failed to fetch admins for cancellation notification | Error: " + e.getMessage());
        }

        // notify driver if assigned
        Driver driver = reservation.getDriver();
        if (driver != null) {
            try {
                emailService.sendEmail(driver.getUser().getEmail(), subject, text);
            } catch (Exception e) {
                System.err.println("[NotificationService] Failed to send cancellation email to driver: "
                        + driver.getUser().getEmail() + " | Error: " + e.getMessage());
            }
        }
    }

    /**
     * Notifies the assigned driver of a new reservation.
     * @param reservation Reservation
     */
    public void notifyDriverOfAssignedReservation(Reservation reservation) {
        Driver driver = reservation.getDriver();
        if (driver != null) {
            try {
                String subject = "New reservation assigned";
                String text = "You have been assigned a new reservation."
                        + "\n\nFrom: " + reservation.getPickupLocation()
                        + "\nTo: " + reservation.getDropoffLocation()
                        + "\nFor: " + reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName()
                        + "\nVehicle: " + reservation.getVehicleCategory().getName();
                emailService.sendEmail(driver.getUser().getEmail(), subject, text);
            } catch (Exception e) {
                System.err.println("[NotificationService] Failed to send assignment email to driver: "
                        + driver.getUser().getEmail() + " | Error: " + e.getMessage());
            }
        } else {
            System.err.println("[NotificationService] notifyDriverOfAssignedReservation called but no driver assigned to reservation: "
                    + reservation.getId());
        }
    }
}
